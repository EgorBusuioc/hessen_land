package de.ecommerce.security.services;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import java.util.List;

/**
 * @author EgorBusuioc
 * 27.06.2025
 */
@Service
public class BindingResultService {

    public ResponseEntity<String> getErrorMessage(BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<ObjectError> errors = bindingResult.getAllErrors();
            StringBuilder builder = new StringBuilder();
            for (ObjectError error : errors) {
                builder.append(error.getDefaultMessage()).append("\n");
            }
            return ResponseEntity.badRequest().body(builder.toString());
        }
        return ResponseEntity.ok("Worked fine!");
    }
}
