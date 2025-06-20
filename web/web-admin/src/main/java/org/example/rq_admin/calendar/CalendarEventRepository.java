package org.example.rq_admin.calendar;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CalendarEventRepository extends JpaRepository<CalendarEvent, Long>, JpaSpecificationExecutor<CalendarEvent> {
}
