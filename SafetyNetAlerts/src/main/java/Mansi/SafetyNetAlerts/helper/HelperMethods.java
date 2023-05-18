package mansi.safetynetalerts.helper;

import mansi.safetynetalerts.model.Person;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class HelperMethods {

    public static int getAge(String Stringdob) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        int age;
        Date dateOfBirth = dateFormat.parse(Stringdob);
        Calendar dob = Calendar.getInstance();
        dob.setTime(dateOfBirth);

        Calendar today = Calendar.getInstance();
        age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);
        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }
        return age;


    }

    public static boolean containsPersonWithName(List<Person> list, String name) {
        for (Person person : list) {
            if (person.getFirstName().equals(name)) {
                return false;
            }
        }
        return true;
    }
}
