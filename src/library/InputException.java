
package library;

/**
 * This exception is to be used to identify and relable an exception thrown by 
 * input gathering functions
 * @author Sam Stenner, Max Prüstel
 */
class InputException extends Exception {
    /**
     * constructor that has to be added to allow paramterless instantiations
     */
    public InputException(){
        super();
    }
    
    /**
     * Constructor that takes a message to be saved in the message attribute.
     * @param message Error message to be saved.
     */
    public InputException(String message){
        super(message);
    }
    
    /**
     * Constructor that takes a message to be saved in the message attribute and 
     * a Throwable object in the cause attribute.
     * @param message Error message to be saved.
     * @param cause different exception that caused the exception to come into existance
     */
    public InputException(String message, Throwable cause){
        super(message, cause);
    }

}
