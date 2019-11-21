package laurens_wuyts.arduinoiocontrol;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Switch;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class MainActivity extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

          /********************************/
         /*    Define all the buttons    */
        /********************************/
        Switch modoControl = (Switch) findViewById(R.id.modoControl);
        Switch rojoCarros = (Switch) findViewById(R.id.rojoCarros);
        Switch amarilloCarros = (Switch) findViewById(R.id.amarilloCarros);
        Switch verdeCarros = (Switch) findViewById(R.id.verdeCarros);
        Switch rojoPeaton = (Switch) findViewById(R.id.rojoPeaton);
        Switch verdePeaton = (Switch) findViewById(R.id.verdePeaton);



        /*******************************************************/
         /*  Set an onclick/onchange listener for every button  */
        /*******************************************************/
        modoControl.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    /* Switch is led 1 */
                    new Background_get().execute("control=1");
                } else {
                    new Background_get().execute("control=0");
                }
            }
        });

        rojoCarros.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    /* Switch is led 1 */
                    new Background_get().execute("rojoCarros=1");
                } else {
                    new Background_get().execute("rojoCarros=0");
                }
            }
        });

        amarilloCarros.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    /* Toggle button is led 2 */
                    new Background_get().execute("amarilloCarros=1");
                } else {
                    new Background_get().execute("amarilloCarros=0");
                }
            }
        });

        verdeCarros.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    /* Toggle button is led 2 */
                    new Background_get().execute("verdeCarros=1");
                } else {
                    new Background_get().execute("verdeCarros=0");
                }
            }
        });

        rojoPeaton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    /* Toggle button is led 2 */
                    new Background_get().execute("rojoPeaton=1");
                } else {
                    new Background_get().execute("rojoPeaton=0");
                }
            }
        });

        verdePeaton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    /* Toggle button is led 2 */
                    new Background_get().execute("verdePeaton=1");
                } else {
                    new Background_get().execute("verdePeaton=0");
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

        /*****************************************************/
       /*  This is a background process for connecting      */
      /*   to the arduino server and sending               */
     /*    the GET request withe the added data           */
    /*****************************************************/

    private class Background_get extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            try {
                /* Change the IP to the IP you set in the arduino sketch */
                URL url = new URL("http://192.168.137.143/?" + params[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder result = new StringBuilder();
                String inputLine;
                while ((inputLine = in.readLine()) != null)
                    result.append(inputLine).append("\n");

                in.close();
                connection.disconnect();
                return result.toString();

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
