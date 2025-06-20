package org.example.rq_admin.calendar;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/event/calendar")
@RequiredArgsConstructor
@Tag(name = "日历事件管理")
public class CalendarEventController {
    private final CalendarEventService service;

    @Operation(summary = "查询某年某月的所有日历事件")
    @GetMapping
    public List<CalendarEvent> listByMonth(@RequestParam int year,
                                           @RequestParam int month,
                                           @RequestParam(required = false) String event) {
        return service.findByYearAndMonth(year, month, event);
    }

    @Operation(summary = "新增日历事件")
    @PostMapping
    public CalendarEvent add(@RequestBody CalendarEvent event) {
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
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "event 字段不是合法 JSON", e);
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
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "start/end/title 字段解析失败，格式应为 ISO 日期字符串", e);
            }
        }
        if (event.getStart() == null || event.getEnd() == null || event.getTitle() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "start、end、title 字段不能为空");
        }
        // 新增时不允许传 id
        event.setId(null);
        return service.save(event);
    }

    @Operation(summary = "编辑日历事件")
    @PutMapping
    public CalendarEvent edit(@RequestBody CalendarEvent event) {
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
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "event 字段不是合法 JSON", e);
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
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "start/end/title 字段解析失败，格式应为 ISO 日期字符串", e);
            }
        }
        if (event.getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "编辑时必须传递 id 字段");
        }
        if (event.getStart() == null || event.getEnd() == null || event.getTitle() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "start、end、title 字段不能为空");
        }
        // 先查找是否存在该id的记录
        Optional<CalendarEvent> old = service.findById(event.getId());
        if (old.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "要编辑的日历事件不存在");
        }
        // 保留原有createdAt
        event.setCreatedAt(old.get().getCreatedAt());
        return service.save(event);
    }

    @Operation(summary = "删除日历事件")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "根据ID查询日历事件")
    @GetMapping("/{id}")
    public ResponseEntity<CalendarEvent> get(@PathVariable Long id) {
        Optional<CalendarEvent> event = service.findById(id);
        return event.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
}
