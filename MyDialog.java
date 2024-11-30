import java.awt.*;
import java.awt.event.*;

public class MyDialog extends Dialog implements ActionListener{
    MyDialog(Frame parent, String title, String text){
        super(parent, title, true);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        setSize(200,200);
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new Label(text), gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = gbc.HORIZONTAL;
        
        Button b = new Button("Ok");
        b.addActionListener(this);
        add(b, gbc);
        this.setVisible(true);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we){
                dispose();
            }
        });
    }    

    public void actionPerformed(ActionEvent ae){
        dispose();
    }
}
