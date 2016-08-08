import bimplus.api.ApiCore;
import bimplus.api.Wrapper.Divisions;
import bimplus.api.Wrapper.Issues;
import bimplus.api.Wrapper.Projects;
import bimplus.api.Wrapper.Teams;
import bimplus.data.DtoDivision;
import bimplus.data.DtoIssue;
import bimplus.data.DtoProject;
import bimplus.data.DtoTeam;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Cornelius on 03.08.2016.
 */
public class MainApplication
{
    public static void main(String[] args)
    {
        MainWindow window = new MainWindow();
        window.ShowWindow();
    }
}