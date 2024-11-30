import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
class ContactsObject{
    String[] FirstName;
    String[] LastName;
    String[] Age;
    String[] PhoneNumber;
    String[] Gender;
    String[] Address;
    String[] Email;
    boolean empty;

    public ContactsObject(String[] FirstName,
    String[] LastName,
    String[] Age,
    String[] PhoneNumber,
    String[] Gender,
    String[] Address,
    String[] Email, 
    Boolean empty){
        this.FirstName = FirstName;
        this.LastName = LastName;
        this.Age = Age;
        this.PhoneNumber = PhoneNumber;
        this.Gender = Gender;
        this.Address = Address;
        this.Email = Email;
        this.empty = empty;
    }
}
public class XMLHandler{
    public static ContactsObject retrieveAllData(){
        String data = readXML();
        StringBuffer details = new StringBuffer();
        String firstNameRegex = "(<Contact>)((\\n.*?)*)(<\\/Contact>)";
        Pattern firstNamePattern = Pattern.compile(firstNameRegex);
        Matcher firstNameMatcher = firstNamePattern.matcher(data);
        
        while(firstNameMatcher.find()){
            details.append(firstNameMatcher.group(2));
        }

        StringBuffer FirstName = new StringBuffer();
        StringBuffer LastName =  new StringBuffer();
        StringBuffer Age =  new StringBuffer();
        StringBuffer PhoneNumber =  new StringBuffer();
        StringBuffer Gender =  new StringBuffer();
        StringBuffer Address =  new StringBuffer();
        StringBuffer Email =  new StringBuffer();

        String regex = "(<FirstName>)(.*)(<\\/FirstName>)";
        Pattern regexPattern = Pattern.compile(regex);
        Matcher regexMatcher = regexPattern.matcher(details);
        while(regexMatcher.find()){
            FirstName.append(regexMatcher.group(2)+" ");
        }

         regex = "(<LastName>)(.*)(<\\/LastName>)";
         regexPattern = Pattern.compile(regex);
         regexMatcher = regexPattern.matcher(details);
        while(regexMatcher.find()){
            LastName.append(regexMatcher.group(2)+" ");
        }

         regex = "(<PhoneNumber>)(.*)(<\\/PhoneNumber>)";
         regexPattern = Pattern.compile(regex);
         regexMatcher = regexPattern.matcher(details);
        while(regexMatcher.find()){
            PhoneNumber.append(regexMatcher.group(2)+" end");
        }

         regex = "(<Age>)(.*)(<\\/Age>)";
         regexPattern = Pattern.compile(regex);
         regexMatcher = regexPattern.matcher(details);
        while(regexMatcher.find()){
            Age.append(regexMatcher.group(2)+" ");
        }

        regex = "(<Gender>)(.*)(<\\/Gender>)";
        regexPattern = Pattern.compile(regex);
        regexMatcher = regexPattern.matcher(details);
        while(regexMatcher.find()){
            Gender.append(regexMatcher.group(2)+" ");
        }

         regex = "(<Email>)(.*)(<\\/Email>)";
         regexPattern = Pattern.compile(regex);
         regexMatcher = regexPattern.matcher(details);
        while(regexMatcher.find()){
            Email.append(regexMatcher.group(2)+" ");
        }

         regex = "(<Address>)(.*)(<\\/Address>)";
         regexPattern = Pattern.compile(regex);
         regexMatcher = regexPattern.matcher(details);
        while(regexMatcher.find()){
            Address.append(regexMatcher.group(2)+" ,end");
        }
        ContactsObject contactList = new ContactsObject(FirstName.toString().split(" "), LastName.toString().split(" "),Age.toString().split(" "),PhoneNumber.toString().split(" end"),Gender.toString().split(" "),Address.toString().split(" ,end"),Email.toString().split(" "), false);
        return contactList;
    }
    public static boolean verifyConsistency(String data, String email){
        StringBuffer emails = new StringBuffer();
        String emailRegex = "(<Email>)(.*)(<\\/Email>)";
        Pattern emailPattern = Pattern.compile(emailRegex);
        Matcher emailMatcher = emailPattern.matcher(data);
        while(emailMatcher.find()){
            emails.append(emailMatcher.group(2)+" ");
        }
        String emailList = emails.toString();
        if(emailList.contains(email)){
            System.out.println("Same email found");
            return false;
        }
        return true;
    }
    public static boolean insertNewContact(String FirstName,String  LastName,String  Age,String  PhoneNumber,String Gender,String Address, String Email){
        String data = readXML();
        String firstNameRegex = "(<Contacts>)((\\n.*)*)(</Contacts>)";
        Pattern firstNamePattern = Pattern.compile(firstNameRegex);
        Matcher firstNameMatcher = firstNamePattern.matcher(data);
        StringBuffer contacts = new StringBuffer();
        contacts.append("<Contacts>");
        while(firstNameMatcher.find()){
            contacts.append(firstNameMatcher.group(2));
        }
        
        String fname = FirstName;
        String lname = LastName;
        String phn = PhoneNumber;
        String email = Email;
        String address = Address;
        String age = Age;
        String gender = Gender;

        // (<Email>)(.*)(<\/Email>)
        if(!verifyConsistency(data, email)){
            return false;
        }
        StringBuffer newContact = new StringBuffer("\n\t<Contact>\n");
        newContact.append("\t\t<FirstName>"+fname+"</FirstName>\n");
        newContact.append("\t\t<LastName>"+lname+"</LastName>\n");
        newContact.append("\t\t<PhoneNumber>"+phn+"</PhoneNumber>\n");
        newContact.append("\t\t<Age>"+age+"</Age>\n");
        newContact.append("\t\t<Gender>"+gender+"</Gender>\n");
        newContact.append("\t\t<Email>"+email+"</Email>\n");
        newContact.append("\t\t<Address>"+address+"</Address>\n");
        newContact.append("\t</Contact>");
        contacts.append(newContact);
        contacts.append("\n</Contacts>");
        try{
        File f = new File("ContactsData.xml");
        FileOutputStream fOverWrite = new FileOutputStream(f);
        fOverWrite.write(contacts.toString().getBytes());
        fOverWrite.close();
        }
        catch(Exception e){}
        return true;
    }

    public static String retrieveByEmail(String email, String data){
        String emailRegex = "(<Contact>)((\\n.*?){6}("+email+")(.*\\n){2}(\\s*))(<\\/Contact>)";
        Pattern emailPattern = Pattern.compile(emailRegex);
        Matcher emailMatcher = emailPattern.matcher(data);
        StringBuffer startEnd = new StringBuffer();
        while(emailMatcher.find()){
            startEnd.append(emailMatcher.start()+" ");
            startEnd.append(emailMatcher.end());
        }
        return startEnd.toString();
    }

    public static ContactsObject extractContactDetails(String data, String email){
        String startEndIndex = XMLHandler.retrieveByEmail(email, data);
        if(!startEndIndex.isEmpty()){
        String[] index = startEndIndex.split(" ");
        int start = Integer.parseInt(index[0]);
        int end = Integer.parseInt(index[1]);
        String retrievedContact = data.substring(start, end);

        StringBuffer FirstName = new StringBuffer();
        StringBuffer LastName =  new StringBuffer();
        StringBuffer Age =  new StringBuffer();
        StringBuffer PhoneNumber =  new StringBuffer();
        StringBuffer Gender =  new StringBuffer();
        StringBuffer Address =  new StringBuffer();
        StringBuffer Email =  new StringBuffer();

        String regex = "(<FirstName>)(.*)(<\\/FirstName>)";
        Pattern regexPattern = Pattern.compile(regex);
        Matcher regexMatcher = regexPattern.matcher(retrievedContact);
        while(regexMatcher.find()){
            FirstName.append(regexMatcher.group(2)+" ");
        }

         regex = "(<LastName>)(.*)(<\\/LastName>)";
         regexPattern = Pattern.compile(regex);
         regexMatcher = regexPattern.matcher(retrievedContact);
        while(regexMatcher.find()){
            LastName.append(regexMatcher.group(2)+" ");
        }

         regex = "(<PhoneNumber>)(.*)(<\\/PhoneNumber>)";
         regexPattern = Pattern.compile(regex);
         regexMatcher = regexPattern.matcher(retrievedContact);
        while(regexMatcher.find()){
            PhoneNumber.append(regexMatcher.group(2)+" end");
        }

         regex = "(<Age>)(.*)(<\\/Age>)";
         regexPattern = Pattern.compile(regex);
         regexMatcher = regexPattern.matcher(retrievedContact);
        while(regexMatcher.find()){
            Age.append(regexMatcher.group(2)+" ");
        }

        regex = "(<Gender>)(.*)(<\\/Gender>)";
        regexPattern = Pattern.compile(regex);
        regexMatcher = regexPattern.matcher(retrievedContact);
        while(regexMatcher.find()){
            Gender.append(regexMatcher.group(2)+" ");
        }

         regex = "(<Email>)(.*)(<\\/Email>)";
         regexPattern = Pattern.compile(regex);
         regexMatcher = regexPattern.matcher(retrievedContact);
        while(regexMatcher.find()){
            Email.append(regexMatcher.group(2)+" ");
        }

         regex = "(<Address>)(.*)(<\\/Address>)";
         regexPattern = Pattern.compile(regex);
         regexMatcher = regexPattern.matcher(retrievedContact);
        while(regexMatcher.find()){
            Address.append(regexMatcher.group(2)+" ,end");
        }
        ContactsObject contactList = new ContactsObject(FirstName.toString().split(" "), LastName.toString().split(" "),Age.toString().split(" "),PhoneNumber.toString().split(" end"),Gender.toString().split(" "),Address.toString().split(" ,end"),Email.toString().split(" "), false);
        return contactList;
        }
        return new ContactsObject(null, null, null, null, null, null, null, true);

    }
    public static String replaceData(int start, int end, String data, String FirstName, String LastName, String PhoneNumber, String Age,
    String Gender, String Email, String Address){
        StringBuffer oldData = new StringBuffer();
        oldData.append(data.substring(start, end));
        String newData = oldData.toString();
        String regex = "(<FirstName>)(.*)(<\\/FirstName>)";
        Pattern regexPattern = Pattern.compile(regex);
        Matcher regexMatcher = regexPattern.matcher(newData);
        while(regexMatcher.find()){
            if(regexMatcher.group(2).equalsIgnoreCase(FirstName)){

            }else{
                newData = newData.replaceAll("(<FirstName>)(.*)(<\\/FirstName>)", "<FirstName>"+FirstName+"</FirstName>");
            }
        }

         regex = "(<LastName>)(.*)(<\\/LastName>)";
         regexPattern = Pattern.compile(regex);
         regexMatcher = regexPattern.matcher(newData);
        while(regexMatcher.find()){
            if(regexMatcher.group(2).equalsIgnoreCase(LastName)){
            }else{
                newData = newData.replaceAll("(<LastName>)(.*)(<\\/LastName>)", "<LastName>"+LastName+"</LastName>");
            }
        }

         regex = "(<PhoneNumber>)(.*)(<\\/PhoneNumber>)";
         regexPattern = Pattern.compile(regex);
         regexMatcher = regexPattern.matcher(newData);
        while(regexMatcher.find()){
            if(regexMatcher.group(2).equalsIgnoreCase(PhoneNumber)){
            }else{
                newData = newData.replaceAll("(<PhoneNumber>)(.*)(<\\/PhoneNumber>)", "<PhoneNumber>"+PhoneNumber+"</PhoneNumber>");
            }
        }

         regex = "(<Age>)(.*)(<\\/Age>)";
         regexPattern = Pattern.compile(regex);
         regexMatcher = regexPattern.matcher(newData);
        while(regexMatcher.find()){
            if(regexMatcher.group(2).equalsIgnoreCase(Age)){
            }else{
                newData = newData.replaceAll("(<Age>)(.*)(<\\/Age>)", "<Age>"+Age+"</Age>");
            }
        }

        regex = "(<Gender>)(.*)(<\\/Gender>)";
        regexPattern = Pattern.compile(regex);
        regexMatcher = regexPattern.matcher(newData);
        while(regexMatcher.find()){
            if(regexMatcher.group(2).equalsIgnoreCase(Gender)){
            }else{
                newData = newData.replaceAll("(<Gender>)(.*)(<\\/Gender>)", "<Gender>"+Gender+"</Gender>");
            }
        }

         regex = "(<Email>)(.*)(<\\/Email>)";
         regexPattern = Pattern.compile(regex);
         regexMatcher = regexPattern.matcher(newData);
        while(regexMatcher.find()){
            if(regexMatcher.group(2).equalsIgnoreCase(Email)){
            }else{
                newData = newData.replaceAll("(<Email>)(.*)(<\\/Email>)", "<Email>"+Email+"</Email>");
            }
        }

         regex = "(<Address>)(.*)(<\\/Address>)";
         regexPattern = Pattern.compile(regex);
         regexMatcher = regexPattern.matcher(data);
        while(regexMatcher.find()){
            if(regexMatcher.group(2).equalsIgnoreCase(Address)){
            }else{
                newData = newData.replaceAll("(<Address>)(.*)(<\\/Address>)", "<Address>"+Address+"</Address>");
            }
        }
        
        return newData;
    }
    public static boolean editByEmail( String FirstName, String LastName, String PhoneNumber, String Age,
    String Gender, String newEmail, String Address, String oldEmail){
       
        String data = readXML();
        if(!verifyConsistency(data, newEmail))
            return false;
        String startEndIndex = XMLHandler.retrieveByEmail(oldEmail, data);
        
        if(startEndIndex.isEmpty()){return false;}
        
        String[] index = startEndIndex.split(" ");
        int start = Integer.parseInt(index[0]);
        int end = Integer.parseInt(index[1]);
        String alteredData = XMLHandler.replaceData(start, end, data, FirstName, LastName, PhoneNumber, Age, Gender, newEmail, Address);
        StringBuffer newData = new StringBuffer();
        newData.append(data.substring(0, start));
        newData.append(alteredData);
        newData.append(data.substring(end, data.length()));
        try{
            File f = new File("ContactsData.xml");
            FileOutputStream fOverWrite = new FileOutputStream(f);
            fOverWrite.write(newData.toString().getBytes());
            fOverWrite.close();
            }
            catch(Exception e){}
        return true;
    }

    public static boolean deleteByEmail(String email){
        String data = readXML();
        String startEndIndex = XMLHandler.retrieveByEmail(email, data);
        try{
            if(startEndIndex.isEmpty())return false;
        }
        catch(Exception e){
            System.out.println("Email not found to be deleted");
        }
        String[] index = startEndIndex.split(" ");
        
        int start = Integer.parseInt(index[0]);
        int end = Integer.parseInt(index[1]);
        StringBuffer newData = new StringBuffer();
        newData.append(data.substring(0, start-1));
        newData.append(data.substring(end+1, data.length()));
        try{
            File f = new File("ContactsData.xml");
            FileOutputStream fOverWrite = new FileOutputStream(f);
            fOverWrite.write(newData.toString().getBytes());
            fOverWrite.close();
            }
            catch(Exception e){}
            return true;
    }
    public static String readXML(){
        StringBuffer data = new StringBuffer();
        
        try{
        File f = new File("ContactsData.xml");
        FileInputStream fis = new FileInputStream(f);
        BufferedReader br = new BufferedReader(new InputStreamReader(fis));
        
        String line = "";
        while((line = br.readLine())!=null){
            data.append(line).append("\n");
        }
            br.close();
            fis.close();
        }
        catch(Exception e){}
        return data.toString();
    }
}
