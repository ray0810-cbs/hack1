package com.example.oreohack.eventos;

import com.example.oreohack.entidades.ReportRequest;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class ReportRequestedEvent extends ApplicationEvent {
    private final ReportRequest reportRequest;

    public ReportRequestedEvent(Object source, ReportRequest reportRequest) {
        super(source);
        this.reportRequest = reportRequest;
    }
}
