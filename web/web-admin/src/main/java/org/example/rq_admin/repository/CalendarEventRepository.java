package org.example.rq_admin.repository;

import org.example.rq_admin.entity.CalendarEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface CalendarEventRepository extends JpaRepository<CalendarEvent, Long>, JpaSpecificationExecutor<CalendarEvent> {
}
