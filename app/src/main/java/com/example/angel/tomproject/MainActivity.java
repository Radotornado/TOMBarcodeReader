/*
 MIT License
Copyright (c) [2016] [Angel Marinov, Radoslav Mandev and Mihail Tsvetogorov]
Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:
The above copyright notice and this permission notice shall be included in all
copies or substantial SerialPortions of the Software.
THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/

package com.example.angel.tomproject;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.os.AsyncTask;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;




public class MainActivity extends AppCompatActivity implements OnClickListener {
    private String scanContent = null;
    private String title;

    private Button scanBtn;
    private TextView formatTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        scanBtn = (Button) findViewById(R.id.scan_button);
        formatTxt = (TextView) findViewById(R.id.scan_format);
        scanBtn.setOnClickListener(this);
        setTitle("Баркод скенер");
    }

    public void onClick(View v) {
        if (v.getId() == R.id.scan_button) { // getting the barcode numbers
            IntentIntegrator scanIntegrator = new IntentIntegrator(this);
            scanIntegrator.initiateScan();
        }
    }



    private class Title extends AsyncTask<Void, Void, Void> { // getting the website search result title
        protected Void doInBackground(Void... params) {
            try {
                Document document = Jsoup.connect("http://barcode.bg/barcode/BG/%D0%98%D0%BD%D1%84%D0%BE%D1%80%D0%BC%D0%B0%D1%86%D0%B8%D1%8F-%D0%B7%D0%B0-%D0%B1%D0%B0%D1%80%D0%BA%D0%BE%D0%B4.htm?barcode="+scanContent).get();
                title = document.title(); // getting the html document and taking its title

            } catch (java.io.IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }


    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanningResult != null) {
            scanContent = scanningResult.getContents();

            new Title().execute();
            try {
                while (title == null) { // waiting for search
                }
                int position = title.indexOf("- Баркод:"); // removing not usefull information
                title = title.substring(0, position);
                title = title.replace("/", " ").replace(".", " ");
            }catch(Exception e) {
                title = "Продуктът не е намерен!"; // when the item is not in database of barcode.bg
            }
            formatTxt.setText(title);
            title=null;

        } else {
            Toast toast = Toast.makeText(getApplicationContext(),
                    "No scan data received!", Toast.LENGTH_SHORT); // if nothing is scanned
            toast.show();
        }




    }

}

