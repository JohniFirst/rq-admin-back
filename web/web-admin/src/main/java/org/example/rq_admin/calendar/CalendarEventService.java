package org.example.rq_admin.calendar;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CalendarEventService {
    private final CalendarEventRepository repository;

    public Page<CalendarEvent> findAll(JsonNode event, Pageable pageable) {
        Specification<CalendarEvent> spec = (root, query, cb) -> {
            if (event != null) {
                // 简单模糊查询 event json 字符串
                return cb.like(cb.function("json_extract", String.class, root.get("event"), cb.literal("$")), "%" + event.toString() + "%");
            }
            return cb.conjunction();
        };
        return repository.findAll(spec, pageable);
    }

    @Transactional
    public CalendarEvent save(CalendarEvent event) {
        return repository.save(event);
    }

    @Transactional
    public void delete(Long id) {
        repository.deleteById(id);
    }

    public Optional<CalendarEvent> findById(Long id) {
        return repository.findById(id);
    }

    public List<CalendarEvent> findByYearAndMonth(int year, int month, String event) {
        YearMonth ym = YearMonth.of(year, month);
        LocalDateTime start = ym.atDay(1).atStartOfDay();
        LocalDateTime end = ym.atEndOfMonth().atTime(23, 59, 59);
        Specification<CalendarEvent> spec = (root, query, cb) -> {
            var predicates = new java.util.ArrayList<Predicate>();
            predicates.add(cb.greaterThanOrEqualTo(root.get("start"), start));
            predicates.add(cb.lessThanOrEqualTo(root.get("end"), end));
            if (event != null && !event.isEmpty()) {
                predicates.add(cb.like(cb.function("json_extract", String.class, root.get("event"), cb.literal("$")), "%" + event + "%"));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        return repository.findAll(spec);
    }
}
