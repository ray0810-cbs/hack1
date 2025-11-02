package com.example.oreohack.eventos;

import com.example.oreohack.dto.request.ReportRequestDTO;
import com.example.oreohack.entidades.UserClass;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class ReportRequestedEvent extends ApplicationEvent {

    private final ReportRequestDTO request;
    private final UserClass user;

    public ReportRequestedEvent(Object source, ReportRequestDTO request, UserClass user) {
        super(source);
        this.request = request;
        this.user = user;
    }
}

