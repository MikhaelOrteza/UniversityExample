package data_access;


import entity.Person;
import entity.Student;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
/*
How does data access work here?

 */

public class UniversityDataAccessObject {
    // personFile and studentFile will be storing the information.
    private final File personFile;
    private final File studentFile;
    // Caches information into memory. Whenever we save, we write back to the persistance on top

    // UTORid to Person
    private final Map<String, Person> persons = new HashMap<>();

    /**
     * Read the Person and Student objects in CSV files named personFilename and studentFilename.
     *
     * @param personFilename  the Person CSV file
     * @param studentFilename the Student CSV file
     * @throws IOException when there is an error reading either file
     */

    // Creates files for the first time and tries to read it.
    public UniversityDataAccessObject(String personFilename, String studentFilename) throws IOException {
        this.personFile = new File(personFilename);
        this.studentFile = new File(studentFilename);

        if (this.personFile.exists()) {
            readPersonFile(personFile);
        }

        if (this.studentFile.exists()) {
            readStudentFile(studentFile);
        }
    }

    private void readStudentFile(File studentFile) throws IOException {
        // Reading a CSV file in Java. If you want to read a file, you need to create a file reader.
        // Java wraps the file reader with a buffer reader that allows us to read a line in the file, which is needed for
        // CSV.
        try (BufferedReader reader = new BufferedReader(new FileReader(this.studentFile))) {

            String row;
            while ((row = reader.readLine()) != null) { // Read a new row as long as its not empty.
                // "lastfirs,First Middle Last"
                String[] cols = row.split(",");
                String utorid = cols[0];
                String saveName = cols[1];
                String[] names = saveName.split(" ");
                String studentNumber = cols[2]; // Students have an extra line of code to capture the student number.

                Person p = new Student(names, utorid, studentNumber);
                persons.put(utorid, p); //Adds the entity into our program.
            }
        }
    }

    private void readPersonFile(File personFile) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(this.personFile))) {

            String row;
            while ((row = reader.readLine()) != null) {
                // "lastfirs,First Middle Last"
                String[] cols = row.split(",");
                String utorid = cols[0];
                String saveName = cols[1];
                String[] names = saveName.split(" ");

                Person p = new Person(names, utorid);
                persons.put(utorid, p);
            }
        }
    }

    public void save(Person person) {
        persons.put(person.getUtorid(), person); // First, we need to update the map. Basically updates Java's entity.
        this.save(); // Saves it to the CSV file. An issue with this is that if we change one person, we have to update
        // the entire file, which can be problematic when the files are already big.
    }

    private void save() {
        try {
            // Will update the files by reseting them and adding everything again.
            BufferedWriter personWriter = new BufferedWriter(new FileWriter(personFile));
            BufferedWriter studentWriter = new BufferedWriter(new FileWriter(studentFile));
            for (Person person : persons.values()) {

                // Combines all the information of the person into a string.
                String saveName = String.join(" ", person.getName());

                if (person instanceof Student) {
                    Student s = (Student) person;
                    // Formats a string with the person's utorid, name, and student id.
                    // The detail of data persistance is now being saved into the prooram with the code below,
                    String line = "%s,%s,%s".formatted(person.getUtorid(), saveName, s.getStudentID());
                    // Adds the name to the file
                    studentWriter.write(line);
                    studentWriter.write("\n");
                } else {
                    // formats the string, just like above.
                    String line = "%s,%s".formatted(person.getUtorid(), saveName);
                    personWriter.write(line);
                    personWriter.write("\n");
                }
            }

            personWriter.close();
            studentWriter.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Person get(String utorid) {
        return persons.get(utorid);
    }

}
