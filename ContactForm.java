import java.awt.*;
import java.awt.event.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;

public class ContactForm extends Frame implements ActionListener {
    Label firstNameLabel, lastNameLabel, ageLabel, genderLabel, phoneLabel, emailLabel, addressLabel, messageLabel, saveStatusLabel;
    TextField firstNameText, lastNameText, ageText, phoneText, emailText, searchField;
    TextArea addressText;
    Choice genderChoice;
    Button submitButton, showDataButton, searchButton, backButton,saveButton, deleteButton;
    String oldEmail;
    JTable table;
    Panel dataCard = new Panel();
    Panel contactCard = new Panel();
    Panel baseCard = new Panel();
    CardLayout cardLO = new CardLayout();

    public ContactForm() {
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                dispose();
            }
        });

        baseCard.setLayout(cardLO);

       
        contactCard.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        firstNameLabel = new Label("First Name:");
        firstNameText = new TextField(20);

        lastNameLabel = new Label("Last Name:");
        lastNameText = new TextField(20);

        ageLabel = new Label("Age:");
        ageText = new TextField(5);

        genderLabel = new Label("Gender:");
        genderChoice = new Choice();
        genderChoice.add("Male");
        genderChoice.add("Female");
        genderChoice.add("Other");

        phoneLabel = new Label("Phone Number:");
        phoneText = new TextField(15);

        emailLabel = new Label("Email:");
        emailText = new TextField(20);

        addressLabel = new Label("Address:");
        addressText = new TextArea(3, 20);

        messageLabel = new Label();
        messageLabel.setForeground(Color.RED);

        submitButton = new Button("Submit");
        submitButton.addActionListener(this);

        showDataButton = new Button("View All Data");
        showDataButton.addActionListener(this);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        contactCard.add(firstNameLabel, gbc);
        gbc.gridx = 1;
        contactCard.add(firstNameText, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        contactCard.add(lastNameLabel, gbc);
        gbc.gridx = 1;
        contactCard.add(lastNameText, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        contactCard.add(ageLabel, gbc);
        gbc.gridx = 1;
        contactCard.add(ageText, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        contactCard.add(genderLabel, gbc);
        gbc.gridx = 1;
        contactCard.add(genderChoice, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        contactCard.add(phoneLabel, gbc);
        gbc.gridx = 1;
        contactCard.add(phoneText, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        contactCard.add(emailLabel, gbc);
        gbc.gridx = 1;
        contactCard.add(emailText, gbc);

        gbc.gridx = 0;
        gbc.gridy = 6;
        contactCard.add(addressLabel, gbc);
        gbc.gridx = 1;
        contactCard.add(addressText, gbc);

        gbc.gridx = 1;
        gbc.gridy = 7;
        gbc.anchor = GridBagConstraints.CENTER;
        contactCard.add(submitButton, gbc);

        gbc.gridx = 1;
        gbc.gridy = 8;
        contactCard.add(messageLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 9;
        gbc.anchor = GridBagConstraints.CENTER;
        contactCard.add(showDataButton, gbc);

        baseCard.add(contactCard, "ContactForm");

        dataCard.setLayout(new GridBagLayout());
        baseCard.add(dataCard, "Data");
        add(baseCard);

        setTitle("Contact Form");
        setSize(400, 500);
        setVisible(true);
    }

    public void actionPerformed(ActionEvent e){
        if (e.getActionCommand().equals("Submit")) {
            if (firstNameText.getText().isEmpty() || lastNameText.getText().isEmpty() ||
                ageText.getText().isEmpty() || phoneText.getText().isEmpty() ||
                emailText.getText().isEmpty() || addressText.getText().isEmpty()) {
                messageLabel.setText("All fields are required.");
            } else {
                String FirstName = firstNameText.getText();
                String LastName = lastNameText.getText();
                try {
                    int age = Integer.parseInt(ageText.getText());
                    if (age <= 0) {
                        messageLabel.setText("Age must be a positive number.");
                        return;
                    }
                } catch (NumberFormatException ex) {
                    messageLabel.setText("Age must be a valid number.");
                    return;
                }
                String Age = ageText.getText();
                String PhoneNumber = phoneText.getText();
                String Email = emailText.getText();
                String Address = addressText.getText();
                String Gender = genderChoice.getSelectedItem();

                if (!validateDetails(Email, PhoneNumber)) {
                    messageLabel.setText("Email or Phone Number format is not valid.");
                    return;
                }
                messageLabel.setForeground(Color.GREEN);
                messageLabel.setText("Contact saved successfully.");
                if (!XMLHandler.insertNewContact(FirstName, LastName, Age, PhoneNumber, Gender, Address, Email)) {
                    messageLabel.setForeground(Color.RED);
                    messageLabel.setText("Email already exists.");
                }
            }
        }
        if (e.getActionCommand().equals("View All Data")) {
            ContactsObject contactList = XMLHandler.retrieveAllData();
            String[] FirstNames = contactList.FirstName;
            String[] LastNames = contactList.LastName;
            String[] Ages = contactList.Age;
            String[] PhoneNumbers = contactList.PhoneNumber;
            String[] Genders = contactList.Gender;
            String[] Addresses = contactList.Address;
            String[] Emails = contactList.Email;

            int rowCount = FirstNames.length;
            String[][] data = new String[rowCount][7];
            for (int i = 0; i < rowCount; i++) {
                data[i][0] = FirstNames[i];
                data[i][1] = LastNames[i];
                data[i][2] = Ages[i];
                data[i][3] = PhoneNumbers[i];
                data[i][4] = Genders[i];
                data[i][5] = Addresses[i];
                data[i][6] = Emails[i];
            }

            String colHeads[] = {"Firstname", "Lastname", "Age", "Phone-number", "Gender", "Address", "Email"};
            table = new JTable(data, colHeads);
            table.getTableHeader().setReorderingAllowed(false);
            table.setEnabled(false);
            int v = ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED;
            int h = ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED;
            JScrollPane jsp = new JScrollPane(table, v, h);

            dataCard.removeAll();
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.gridwidth = 2;
            gbc.fill = GridBagConstraints.HORIZONTAL;

            Label searchLabel = new Label("Enter email to find and edit:");
            dataCard.add(searchLabel, gbc);

            searchField = new TextField(30);
            gbc.gridx = 0;
            gbc.gridy = 1;
            gbc.gridwidth = 1;
            dataCard.add(searchField, gbc);

            searchButton = new Button("Search");
            searchButton.setActionCommand("Search");
            searchButton.addActionListener(this);
            gbc.gridx = 2;
            gbc.gridy = 1;
            gbc.fill = gbc.NONE;
            dataCard.add(searchButton, gbc);


            gbc.gridx = 0;
            gbc.gridy = 2;
            gbc.gridwidth = 1;
            gbc.fill = GridBagConstraints.BOTH;
            gbc.weightx = 1;
            gbc.weighty = 1;
            dataCard.add(jsp, gbc);

          
          
            backButton = new Button("Back");
            backButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ae) {
                    cardLO.show(baseCard, "ContactForm");
                    contactCard.revalidate();
                    contactCard.repaint();
                }
            });
            gbc.gridx = 0;
            gbc.gridy = 3;
            gbc.gridwidth = 1;
            gbc.gridheight = 2;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.anchor = GridBagConstraints.NORTH;
            dataCard.add(backButton, gbc);

            cardLO.show(baseCard, "Data");
            dataCard.validate();
            dataCard.repaint();
        }
        if (e.getActionCommand().equals("Search")) {
            dataCard.removeAll();
        
            if (searchField.getText().length() != 0) {
                
                String email = searchField.getText();
                ContactsObject retrievedContact = XMLHandler.extractContactDetails(XMLHandler.readXML(), email);
        
                if (retrievedContact.empty) {
                    Label noResultLabel = new Label("No contact found with the provided email.");
                    GridBagConstraints gbc = new GridBagConstraints();
                    gbc.gridx = 0;
                    gbc.gridy = 3;
                    gbc.gridwidth = 3;
                    gbc.anchor = GridBagConstraints.CENTER;
                    dataCard.add(noResultLabel, gbc);
                } else {
                   
                    String[][] data2D = {
                        {retrievedContact.FirstName[0], retrievedContact.LastName[0], retrievedContact.Age[0],
                         retrievedContact.PhoneNumber[0], retrievedContact.Gender[0], retrievedContact.Address[0], retrievedContact.Email[0]}
                    };
                    oldEmail = retrievedContact.Email[0];
                    String colHeads[] = {"Firstname", "Lastname", "Age", "Phone-number", "Gender", "Address", "Email"};
                    table = new JTable(data2D, colHeads);
                    table.setEnabled(true);
                    table.getTableHeader().setReorderingAllowed(false);
  
                    GridBagConstraints gbc = new GridBagConstraints();
                    gbc.insets = new Insets(5, 5, 5, 5);
                    gbc.gridx = 0;
                    gbc.gridy = 2;
                    gbc.gridwidth = 1;
                    gbc.fill = GridBagConstraints.BOTH;
                    gbc.weightx = 1;
                    gbc.weighty = 1;
                    int v = ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED;
                    int h = ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED;
                    JScrollPane jsp = new JScrollPane(table, v, h);
                    dataCard.add(jsp, gbc);
                }
            }
        
           
            GridBagConstraints gbc = new GridBagConstraints();

            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.gridwidth = 2;
            gbc.fill = GridBagConstraints.HORIZONTAL;

            dataCard.add(new Label("Enter the email to find and edit"), gbc);
        
            gbc.gridx = 0;
            gbc.gridy = 1;
            gbc.gridwidth = 1;
            dataCard.add(searchField, gbc);
        
            gbc.gridx = 1;
            gbc.gridy = 1;
            searchButton = new Button("Search");
            searchButton.addActionListener(this);
            dataCard.add(searchButton, gbc);

            gbc.gridx = 2;
            dataCard.add(showDataButton, gbc);
            
            gbc.gridx = 3;
            gbc.gridy = 1;
            backButton = new Button("Back");
            backButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    cardLO.show(baseCard, "ContactForm");
                    addShowDataButton();
                }
            });
            dataCard.add(backButton, gbc);

            gbc.gridx = 5;
            gbc.gridy = 1;
            deleteButton = new Button("Delete");
            deleteButton.addActionListener(this);
            dataCard.add(deleteButton, gbc);

            saveButton = new Button("Save Changes");
            gbc.gridx = 4;
            gbc.gridy = 1;
            dataCard.add(saveButton, gbc);
            saveButton.addActionListener(this);
            dataCard.revalidate();
            dataCard.repaint();
        }
        if(e.getActionCommand() == "Save Changes"){
            // Firstname", "Lastname", "Age", "Phone-number", "Gender", "Address", "Email
            String FirstName = (String)table.getValueAt(0, 0);
            String LastName = (String)table.getValueAt(0, 1);
            String Age = (String)table.getValueAt(0, 2);
            String phoneNumber = (String)table.getValueAt(0, 3);
            String Gender = (String)table.getValueAt(0, 4);
            String Address = (String)table.getValueAt(0, 5);
            String Email = (String)table.getValueAt(0, 6);
            if(FirstName.isEmpty() || LastName.isEmpty() || Age.isEmpty() || phoneNumber.isEmpty() || Gender.isEmpty() || Address.isEmpty() || Email.isEmpty()){
                new MyDialog(this, "Field Empty", "All fields are required");
                return;
            }
            if(!validateDetails(Email, phoneNumber)){
                new MyDialog(this, "Field Empty", "Please Check the format of email or phone number");
                return;
            }
            if(XMLHandler.editByEmail(FirstName, LastName, phoneNumber, Age, Gender, Email, Address, oldEmail)){
                new MyDialog(this, "Field Empty", "Contact edited Successfully!!!");
                oldEmail = Email;
            }
            else
                new MyDialog(this, "already exists", "Email already exists!");
        }
        if(e.getActionCommand() == "Delete"){
            String Email = (String)table.getValueAt(0, 6);
            if(XMLHandler.deleteByEmail(Email)){
                cardLO.show(baseCard, "ContactForm");
                addShowDataButton();
                new MyDialog(this, "Contact Deleted", "Contact deleted successfully!!!");
                
                return;
            }
            else{
                new MyDialog(this, "Check email", "Please enter the email properly to delete");
            }
        }
    }

    private void addShowDataButton() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 9;
        gbc.anchor = GridBagConstraints.CENTER;
        contactCard.add(showDataButton, gbc);
        contactCard.revalidate();
        contactCard.repaint();
    }
    
    public static boolean validateDetails(String email, String mobileNumber) {
        String mobileNumberRegex = "^\\+\\d{2}\\s{0,1}[5-9]\\d{9}$";
        Pattern mobileNumberPattern = Pattern.compile(mobileNumberRegex);
        Matcher mobileNumbMatcher = mobileNumberPattern.matcher(mobileNumber);

        String emailRegex = "^([a-zA-Z]([A-Za-z._0-9]+)*)@([0-9A-Za-z\\-_]+).(.\\w{2,3}(.\\w{2})*)$";
        Pattern emailPattern = Pattern.compile(emailRegex);
        Matcher emailMatcher = emailPattern.matcher(email);

        return mobileNumbMatcher.matches() && emailMatcher.matches();
    }

    public static void main(String[] args) {
        new ContactForm();
    }
}
