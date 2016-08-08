import bimplus.api.ApiCore;
import bimplus.api.Wrapper.*;
import bimplus.data.*;
import sun.misc.IOUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.List;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.*;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;

/**
 * Created by Cornelius on 04.08.2016.
 */
public class MainWindow
{
    // Api Connection
    private ApiCore core;

    // UI STUFF
    private JFrame mainFrame;
    private LoginDialog loginDialog;
    private TeamsList teamList;
    private ProjectsList projectsList;
    private DivisionsList divisionsList;
    private DivisionsTable divisionsTable;
    private AttachmentsList attachmentsList;
    private IssuesList issuesList;
    private DtoTeam selectedTeam;

    public MainWindow()
    {
        // Login Routine
        loginDialog = new LoginDialog();
        // loginDialog.nameField.setText("cornelius.preidel@googlemail.com");
        // loginDialog.passwordField.setText("germany");
        loginDialog.setSize(400, 150);
        loginDialog.setModal(true);
        loginDialog.setLocationRelativeTo(null);
        loginDialog.setVisible(true);

        // Get the connected ApiCore
        core = loginDialog._core;

        // Init the UI
        mainFrame = new JFrame("MainApplication");
        mainFrame.setSize(500, 500);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setLayout(new GridLayout(5, 1));

        // WebProjectsList webprojectlist = new WebProjectsList(core);
        // mainFrame.add(webprojectlist);

        // Create the APIS
        Teams teamsAPI = new Teams(core);
        final Projects projectsAPI = new Projects(core);
        final Divisions divisionsAPI = new Divisions(core);
        final Attachments attachmentAPI = new Attachments(core);
        final Issues issueAPI = new Issues(core);

        // TEAMS List
        TeamsList teamList = new TeamsList();
        teamList.setTeams(teamsAPI.GetTeams());
        mainFrame.add(teamList);

        // PROJECTS  List
        projectsList = new ProjectsList();
        mainFrame.add(projectsList);

        // DIVISIONS List
        divisionsList = new DivisionsList();
        // mainFrame.add(divisionsList);

        // DIVISIONS Table
        divisionsTable = new DivisionsTable(core);
        mainFrame.add(divisionsTable);

        // Attachments
        attachmentsList = new AttachmentsList();
        mainFrame.add(attachmentsList);

        // Attachments
        issuesList = new IssuesList();
        mainFrame.add(issuesList);

        PropertyChangeListener propertyChangeListener = new PropertyChangeListener()
        {
            public void propertyChange(PropertyChangeEvent propertyChangeEvent)
            {
                String property = propertyChangeEvent.getPropertyName();
                if ("selectedTeam".equals(property))
                {
                    DtoTeam selectedTeam = (DtoTeam) propertyChangeEvent.getNewValue();

                    // Set the global slug
                    core.currentTeam = selectedTeam;
                    projectsList.setProjects(projectsAPI.GetProjects());
                }
                else if ("selectedProject".equals(property))
                {
                    DtoProject selectedProject = (DtoProject) propertyChangeEvent.getNewValue();
                    // Divisions
                    divisionsList.setDivisions(divisionsAPI.GetDivisions(selectedProject.GetId()));
                    divisionsTable.setDivisions(divisionsAPI.GetDivisions(selectedProject.GetId()));
                    // Attachments
                    attachmentsList.setAttachments(attachmentAPI.GetAttachments(selectedProject.GetId()));
                    // Issues
                    issuesList.setIssues(issueAPI.GetIssues(selectedProject.GetId()));
                }
                else if ("selectedDivision".equals(property))
                {
                    DtoDivision selectedDivision = (DtoDivision) propertyChangeEvent.getNewValue();
                    // What to do with the division?
                }
                else if ("selectedIssue".equals(property))
                {
                    DtoIssue selectedDivision = (DtoIssue) propertyChangeEvent.getNewValue();
                    // What to do with the division?
                }
                else {
                    if ("selectedAttachment".equals(property)) {
                        DtoAttachment selectedAttachment = (DtoAttachment) propertyChangeEvent.getNewValue();
                        InputStream stream = attachmentAPI.DownloadAttachment(selectedAttachment.GetId());

                        if (stream != null) {
                            JFileChooser chooser = new JFileChooser();
                            int returnVal = chooser.showSaveDialog(chooser);
                            File file = chooser.getSelectedFile();

                            OutputStream outputStream = null;
                            try {
                                outputStream = new FileOutputStream(file);
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                            try {
                                org.apache.commons.io.IOUtils.copy(stream, outputStream);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            try {
                                outputStream.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }
                    }
                }
            }
        };

        // Add Listener
        teamList.addPropertyChangeListener(propertyChangeListener);
        projectsList.addPropertyChangeListener(propertyChangeListener);
        divisionsList.addPropertyChangeListener(propertyChangeListener);
        divisionsTable.addPropertyChangeListener(propertyChangeListener);
        attachmentsList.addPropertyChangeListener(propertyChangeListener);
        issuesList.addPropertyChangeListener(propertyChangeListener);
    }

    public void ShowWindow()
    {
        mainFrame.setVisible(true);
    }
}
