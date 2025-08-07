package org.example.rq_admin.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.rq_admin.entity.CalendarEvent;
import org.example.rq_admin.response_format.FormatResponseData;
import org.example.rq_admin.service.CalendarEventService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/event/calendar")
@RequiredArgsConstructor
@Tag(name = "日历事件管理")
public class CalendarEventController {
    private final CalendarEventService service;

    @Operation(summary = "查询指定时间范围内的所有日历事件")
    @GetMapping
    public FormatResponseData listByRange(@RequestParam String start,
                                          @RequestParam String end,
                                          @RequestParam(required = false) String event) {
        LocalDateTime startTime;
        LocalDateTime endTime;
        try {
            OffsetDateTime startOffset = OffsetDateTime.parse(start);
            OffsetDateTime endOffset = OffsetDateTime.parse(end);
            startTime = startOffset.atZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
            endTime = endOffset.atZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
        } catch (Exception e) {
            return FormatResponseData.error("start/end 参数格式错误，需为 ISO 日期字符串");
        }
        List<CalendarEvent> calendarEvents = service.findByRange(startTime, endTime, event);
        return FormatResponseData.ok(calendarEvents);
    }

    @Operation(summary = "新增日历事件")
    @PostMapping
    public FormatResponseData add(@RequestBody CalendarEvent event) {
        JsonNode node = null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            if (event.getEvent() != null) {
                if (event.getEvent().isTextual()) {
                    node = mapper.readTree(event.getEvent().asText());
                } else {
                    node = event.getEvent();
                }
            }
        } catch (Exception e) {
            return FormatResponseData.error("event 字段不是合法 JSON");
        }
        if (node != null) {
            DateTimeFormatter dtf = DateTimeFormatter.ISO_DATE_TIME;
            try {
                if (node.has("start") && !node.get("start").isNull()) {
                    event.setStart(LocalDateTime.parse(node.get("start").asText(), dtf));
                }
                if (node.has("end") && !node.get("end").isNull()) {
                    event.setEnd(LocalDateTime.parse(node.get("end").asText(), dtf));
                }
                if (node.has("title") && !node.get("title").isNull()) {
                    event.setTitle(node.get("title").asText());
                }
            } catch (Exception e) {
                return FormatResponseData.error("start/end/title 字段解析失败，格式应为 ISO 日期字符串");
            }
        }
        if (event.getStart() == null || event.getEnd() == null || event.getTitle() == null) {
            return FormatResponseData.error("start、end、title 字段不能为空");
        }
        // 新增时不允许传 id
        event.setId(null);
        CalendarEvent saved = service.save(event);
        return FormatResponseData.ok(saved);
    }

    @Operation(summary = "编辑日历事件")
    @PutMapping
    public FormatResponseData edit(@RequestBody CalendarEvent event) {
        JsonNode node = null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            if (event.getEvent() != null) {
                if (event.getEvent().isTextual()) {
                    node = mapper.readTree(event.getEvent().asText());
                } else {
                    node = event.getEvent();
                }
            }
        } catch (Exception e) {
            return FormatResponseData.error("event 字段不是合法 JSON");
        }
        if (node != null) {
            DateTimeFormatter dtf = DateTimeFormatter.ISO_DATE_TIME;
            try {
                if (node.has("id") && !node.get("id").isNull()) {
                    event.setId(node.get("id").asLong());
                }
                if (node.has("start") && !node.get("start").isNull()) {
                    event.setStart(LocalDateTime.parse(node.get("start").asText(), dtf));
                }
                if (node.has("end") && !node.get("end").isNull()) {
                    event.setEnd(LocalDateTime.parse(node.get("end").asText(), dtf));
                }
                if (node.has("title") && !node.get("title").isNull()) {
                    event.setTitle(node.get("title").asText());
                }
            } catch (Exception e) {
                return FormatResponseData.error("start/end/title 字段解析失败，格式应为 ISO 日期字符串");
            }
        }
        if (event.getId() == null) {
            return FormatResponseData.error("编辑时必须传递 id 字段");
        }
        if (event.getStart() == null || event.getEnd() == null || event.getTitle() == null) {
            return FormatResponseData.error("start、end、title 字段不能为空");
        }
        Optional<CalendarEvent> old = service.findById(event.getId());
        if (old.isEmpty()) {
            return FormatResponseData.error("要编辑的日历事件不存在");
        }
        event.setCreatedAt(old.get().getCreatedAt());
        CalendarEvent saved = service.save(event);
        return FormatResponseData.ok(saved);
    }

    @Operation(summary = "删除日历事件")
    @DeleteMapping("/{id}")
    public FormatResponseData delete(@PathVariable Long id) {
        service.delete(id);
        return FormatResponseData.ok();
    }

    @Operation(summary = "根据ID查询日历事件")
    @GetMapping("/{id}")
    public FormatResponseData get(@PathVariable Long id) {
        Optional<CalendarEvent> event = service.findById(id);
        return event.map(FormatResponseData::ok).orElseGet(() -> FormatResponseData.error("未找到该日历事件"));
    }
}
