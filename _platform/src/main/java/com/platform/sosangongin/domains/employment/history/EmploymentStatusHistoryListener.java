package com.platform.sosangongin.domains.employment.history;

import com.platform.sosangongin.domains.employment.Employment;
import com.platform.sosangongin.domains.employment.EmploymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import static org.springframework.transaction.event.TransactionPhase.BEFORE_COMMIT;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmploymentStatusHistoryListener {

    private final EmploymentStatusHistoryRepository historyRepository;
    private final EmploymentRepository employmentRepository;

    @TransactionalEventListener(phase = BEFORE_COMMIT)
    public void onStatusChanged(EmploymentStatusChangedEvent event) {
        Employment employment = employmentRepository.findById(event.employmentId())
                .orElseThrow(() -> new IllegalStateException("Employment not found: " + event.employmentId()));

        EmploymentStatusHistory history = EmploymentStatusHistory.builder()
                .employment(employment)
                .previousStatus(event.previousStatus())
                .newStatus(event.newStatus())
                .build();

        historyRepository.save(history);
        log.debug("Employment status history saved: {} -> {} (employmentId={})",
                event.previousStatus(), event.newStatus(), event.employmentId());
    }
}
