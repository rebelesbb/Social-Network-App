package com.socialnetwork.socialnetworkapp.domain.validators;

import com.socialnetwork.socialnetworkapp.domain.User;

public class UserValidator implements Validator<User> {
    public UserValidator(){}

    @Override
    public void validate(User entity) throws ValidationException {
        String errors = "";

        // first and last name validations
        if (entity.getFirstName().isEmpty())
            errors += "Invalid firstname\n";
        if (entity.getLastName().isEmpty())
            errors += "Invalid lastname\n";

        // email validations
        boolean isOk = validateEmail(entity);

        if(!isOk)
            errors += "Invalid email\n";

        // password validations
        /*
         * maybe later add some validations concerning the strength of a password
         */

        if(entity.getPassword().length() < 5)
            errors += "Invalid password\n";

        if(!errors.isEmpty())
            throw new ValidationException("User data not valid!\n" + errors);

    }

    /**
     * Validates the format of an email
     * @param entity the user whose email is being validated
     * @return true if the emails has the format mmmmmmmm@mmmmm.mmm, false otherwise
     */
    private static boolean validateEmail(User entity) {
        boolean isOk = true;
        if (entity.getEmail().isEmpty())
            isOk = false;
        else{
            if (entity.getEmail().indexOf('@') == -1)
                isOk = false;
            else{
                String[] splitEmail = entity.getEmail().split("@");
                for(var eComp : splitEmail)
                    if (eComp.isEmpty()) {
                        isOk = false;
                        break;
                    }
                if(splitEmail[1].indexOf('.') == -1)
                    isOk = false;
                else {
                    if(splitEmail[1].indexOf('.') == -1 || splitEmail[1].indexOf('.') == 0 || splitEmail[1].indexOf('.') == splitEmail[1].length() - 1)
                        isOk = false;
                }
            }
        }
        return isOk;
    }


}
