package org.albiongames.bladerunner.vktest;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.*;

import java.sql.*;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.*;
import android.widget.*;

import java.sql.SQLException;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: dair
 * Date: 4/22/11
 * Time: 7:13 PM
 * To change this template use File | Settings | File Templates.
 */
public class VKTest extends Activity
{
    public static final String PREFS_NAME = "BladerunnerPrefs";
    private List<String> nameList = new ArrayList<String>();
    private List<Integer> idList = new ArrayList<Integer>();
    int questionId = 0;
    int sessionId = 0;

    TextView questionView = null;
    ListView listView = null;

    CountDownTimer timer = new CountDownTimer(1000, 1000) {
        @Override
        public void onTick(long l) {
            // do nothing
        }

        @Override
        public void onFinish() {
            runOnUiThread(new Runnable() {
                public void run() {
                    retrieve();
                }
            });
        }
    };

    public void showError(String error)
    {
        AlertDialog alertDialog;
        alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Ошибка");
        alertDialog.setMessage(error);
        alertDialog.show();
    }

    public VKTest()
    {
        try
        {
            Class.forName("org.postgresql.Driver");
        }
        catch (ClassNotFoundException ex)
        {
            Log.d("PSQL", ex.toString());
//            showError(ex.toString());
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.vktest);

        questionView = (TextView)findViewById(R.id.textHeader);
        listView = (ListView)findViewById(R.id.list);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Log.d("onItemClick", "Position = " + String.valueOf(position) + ", id = " + String.valueOf(id));

                String query = "INSERT into vk_session_answer (session_id, question_id, answer_id) values (" +
                        String.valueOf(sessionId) + ", " +
                        String.valueOf(questionId) + ", " +
                        String.valueOf(idList.get(position)) + ")";
                try
                {
                    Connection conn = getConnection();
                    Statement st = conn.createStatement();
                    st.execute(query);
                    conn.close();

                }
                catch (SQLException ex)
                {
                    Log.d("PSQL", ex.getLocalizedMessage());
                    showError(ex.getLocalizedMessage());
                }

                retrieve();
            }
        });

    }

    private void retrieve()
    {
        synchronized (timer)
        {
            Log.d("PSQL", "retrieve() called");
            nameList.clear();
            idList.clear();
            questionId = 0;
            sessionId = 0;

            boolean found = false;
            boolean error = true;
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            String deviceId = prefs.getString("deviceId", "1");

            String subquery = "select SA.session_id, SA.question_id from vk_session_answer SA, vk_session SS where SA.session_id = SS.id and SS.device_id = " + deviceId;
            String query = "select Q.id AS qid, S.id as sid, Q.text from vk_question Q, vk_session S, vk_session_question SQ where " +
                        "S.device_id = " + deviceId + " and " +
                        "S.status = 'A' and " +
                        "Q.id = SQ.question_id and " +
                        "S.id = SQ.session_id and " +
                        "(S.id, Q.id) not in (" + subquery + ") order by SQ.qtime ASC";

            try
            {
                Connection conn = getConnection();
                Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery(query);

                while (rs.next())
                {
                    questionId = rs.getInt("qid");
                    sessionId = rs.getInt("sid");

                    questionView.setText("Вопрос: " + rs.getString("text") + "\n\nВыберите ответ:");

                    ResultSet rs2 = st.executeQuery("select id, text from vk_answer where question_id = " + String.valueOf(rs.getInt("qid")));
                    while (rs2.next())
                    {
                        idList.add(rs2.getInt("id"));
                        nameList.add(rs2.getString("text"));
                    }

                    found = true;

                    break;
                }
                conn.close();
                error = false;
            }
            catch (Throwable ex)
            {
                Log.d("PSQL", ex.getLocalizedMessage());
                showError(ex.getLocalizedMessage());
            }

            ListAdapter adapter = new ArrayAdapter<String>(this, R.layout.list_item, nameList);
            listView.setAdapter(adapter);

            if (!error && !found)
            {
                questionView.setText("Ждём вопроса");
                timer.start();
            }

        }
    }

    private Connection getConnection() throws SQLException
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        String host = prefs.getString("host", "192.168.1.25");
        String port = prefs.getString("port", "5432");
        String dbName = prefs.getString("dbName", "BLADERUNNER");
        String url = "jdbc:postgresql://" + host + ":" + port + "/" + dbName;
        String user = prefs.getString("user", "blademaster");
        String passwd = prefs.getString("passwd", "");
        Properties props = new Properties();
        props.setProperty("user", user);
        props.setProperty("password", passwd);
        DriverManager.setLoginTimeout(5);
        Connection conn = DriverManager.getConnection(url, props);
        return conn;
    }

    public void onResume()
    {
        super.onResume();

        retrieve();
    }

    public void onPause()
    {
        synchronized (timer)
        {
            timer.cancel();
        }
        super.onPause();
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
            case R.id.refresh:
                retrieve();
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
