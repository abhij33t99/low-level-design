package exceptions;

public class PatientNotFoundException extends RuntimeException {
    public PatientNotFoundException(String id) {
        super("patient not found with id: "+ id);
    }
}
