package org.albiongames.bladerunner.vktest;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.database.DataSetObserver;

import java.sql.*;

import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: dair
 * Date: 4/22/11
 * Time: 7:13 PM
 * To change this template use File | Settings | File Templates.
 */
public class VKTest extends ListActivity {

    private List<String> nameList = new ArrayList<String>();

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try
        {
            Class.forName("org.postgresql.Driver");
        }
        catch (ClassNotFoundException ex)
        {
            Log.d("PSQL", ex.toString());
        }
    }

    public void onResume()
    {
        super.onResume();
        String url = "jdbc:postgresql://cats-shadow.spb.ru:5432/BLADERUNNER";
        Properties props = new Properties();
        props.setProperty("user","blademaster");
        props.setProperty("password","gfhjkm");
        nameList.clear();
        try
        {
            Connection conn = DriverManager.getConnection(url, props);

            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT NAME FROM PERSON");

            while (rs.next())
            {
                nameList.add(rs.getString("NAME"));
            }
        }
        catch (SQLException ex)
        {
            Log.d("PSQL", ex.toString());
        }

        ListAdapter adapter = new ArrayAdapter<String>(this, R.layout.list_item, nameList);
        setListAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.vktestmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.settings:
                openSettings();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    protected void openSettings()
    {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }
}
