package com.teamlead.DTO;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ValidationErrorDTO {
    
    private boolean success;
    private String message;
    private List<String> errors = new ArrayList<>();
    private Integer demandeId;
    
    public ValidationErrorDTO(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
    
    public void addError(String error) {
        this.errors.add(error);
    }
}
