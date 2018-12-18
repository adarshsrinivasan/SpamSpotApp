package com.example.adarsh.smstest;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceList;
import com.microsoft.windowsazure.mobileservices.http.OkHttpClientFactory;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;
import com.microsoft.windowsazure.mobileservices.table.sync.MobileServiceSyncContext;
import com.microsoft.windowsazure.mobileservices.table.sync.MobileServiceSyncTable;
import com.squareup.okhttp.OkHttpClient;

import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    public static HashMap<String,String> messages = new HashMap<String, String>();
    public static HashMap<String,String> numbers = new HashMap<String, String>();
    Spams items;
    public static MobileServiceSyncTable<Spams> mMassageData;
    public static MobileServiceTable<Spams> mSTable;
    public static MobileServiceList<Spams> result;
    public static MobileServiceClient mClient;
    int  i,mid = 1,l = 0;
    public static int j = 0;
    Spams item;
    Context context = this;
    byte[] data;
    String sms = "",Body ="",From = "",Type ="", Temp = "", id,query, tempst;
    String[] seg,reqCols,n,idList;
    Cursor ci,smsitems,co, managedCursor;
    ContentResolver cr, numcr;
    ListView lvMsg;
    public static SmsAdapter smsAdapter;
    Data_SmsInbox smsdata, temp;
    Uri inboxURI;
    List list;
    Object[] a;
    View sv;
    FragmentManager fm;
    FragmentTransaction ft;
    FrameLayout fl;
    static boolean first = true;
    public static Classifier<String, String> bayes =
            new BayesClassifier<String, String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            mClient = new MobileServiceClient("https://spamspot.azurewebsites.net", this);
            mClient.setAndroidHttpClientFactory(new OkHttpClientFactory() {
                @Override
                public OkHttpClient createOkHttpClient() {
                    OkHttpClient client = new OkHttpClient();
                    client.setReadTimeout(20, TimeUnit.SECONDS);
                    client.setWriteTimeout(20, TimeUnit.SECONDS);
                    return client;
                }
            });
        } catch (MalformedURLException e) {
            Toast.makeText(this,"Can't Connect to Azure",Toast.LENGTH_SHORT).show();
            Log.e("Azure Error", e.toString());
        }
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_CONTACTS},1);
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.INTERNET},1);
        while(ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED);
        while(ContextCompat.checkSelfPermission(context, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED);
        lvMsg = (ListView)findViewById(R.id.lvMsg);
        //mMassageData = mClient.getSyncTable(Spams.class);
        mSTable = mClient.getTable(Spams.class);
        smsAdapter = new SmsAdapter(context,R.layout.row);
        numcr = context.getContentResolver();
        fm = getSupportFragmentManager();
        fl = (FrameLayout)findViewById(R.id.frameLayout);
        ft = fm.beginTransaction();
        ft.add(R.id.frameLayout,new Intro());
        ft.commit();
        if(first) {
            AsyncTask<Void,Void,Void> task = new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    try {
//                        InputStream in = getAssets().open("sms.txt");
//                        data = new byte[in.available()];
//                        in.read(data);
//                        sms = new String(data);
//                        seg = sms.split("\n");
//                        for (i = 0; i < seg.length; i += 2) {
//                            seg[i] = seg[i].trim();
//                            seg[i] = seg[i].replaceAll("[^\\p{Alpha} ]+","");
//                            if(messages.get(seg[i]) == null){
//                                messages.put(seg[i],seg[i + 1].trim());
//                                item = new Spams();
//                                item.setText(seg[i]);
//                                item.setType(seg[i + 1].trim());
//                                mSTable.insert(item).get();
//                                mid++;
//                            }
//                        }
                        result = mSTable.where().field("Deleted").eq(false).execute().get();
                        for(Spams item : result){
                            messages.put(item.getText(),item.getType());
                        }
                        //Toast.makeText(getApplicationContext(), "Db loaded ", Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Log.e("Main", e.toString() + " " + sms);
                    }
                    return null;
                }
            };
            task.execute();
//            while(task.getStatus() != AsyncTask.Status.FINISHED){
//                Toast.makeText(this,"Wait",Toast.LENGTH_SHORT).show();
//            }
            first = false;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Toast.makeText(getApplicationContext(),"Please Change to your Default SMS app", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
        startActivity(intent);
    }
    public static AsyncTask<Void, Void, Void> sync() {
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    MobileServiceSyncContext syncContext = mClient.getSyncContext();
                    syncContext.push().get();
                    mMassageData.pull(null).get();
                } catch (final Exception e) {
                }
                return null;
            }
        };
        return runAsyncTask(task);
    }
    public static AsyncTask<Void, Void, Void> runAsyncTask(AsyncTask<Void, Void, Void> task) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            return task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            return task.execute();
        }
    }

    public void populate_adapter(){
        j = 0;
        smsAdapter.clean();
        inboxURI = Uri.parse("content://sms/inbox");
        reqCols = new String[] { "_id", "address", "body" };
        cr = getContentResolver();
        ci = cr.query(inboxURI, reqCols, null, null, null);
        while (ci.moveToNext()) {
            id = ci.getString(0);
            From = ci.getString(1);
            Body = ci.getString(2);
            seg = Body.split("\n");
            Body = "";
            for(i = 0 ; i < seg.length ; i++){
                Body += seg[i] + " ";
            }
            Body = Body.trim();
            tempst = Body.replaceAll("[^\\p{Alpha} ]+","");
            seg = Body.split("\\s");
            Type = bayes.classify(Arrays.asList(seg)).getCategory();
            if(contactExists(From.trim()
            )){Type = "positive";}
            if(Type.trim().equals("negative")){j++;}
            if(messages.get(tempst) == null){
                messages.put(tempst,Type.trim());
                item = new Spams();
                item.setText(tempst);
                item.setType(Type.trim());
                try {
                    mSTable.insert(item).get();
                } catch (InterruptedException e) {
                    Log.e("table",e.toString());
                } catch (ExecutionException e) {
                    Log.e("table",e.toString());
                }
                mid++;
            }
            smsdata = new Data_SmsInbox(id,Body,From,Type);
            smsAdapter.add(smsdata);
        }
    }
    public void ps(View v) {
        //Toast.makeText(this,"" + messages.size(),Toast.LENGTH_SHORT).show();
        Thread thread = new Thread(){
            @Override
            public void run() {
                try {
                    synchronized (this) {
                        wait(5000);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        bayes = new BayesClassifier<String, String>();
                                        try {
                                            result = mSTable.where().field("Deleted").eq(false).execute().get();
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                            Log.e("run",e.toString());
                                        } catch (ExecutionException e) {
                                            e.printStackTrace();
                                            Log.e("run",e.toString());
                                        }
                                        for(Spams item : result) {
                                            Temp = item.getText();
                                            Temp = Temp.trim();
                                            Temp = Temp.replaceAll("[^\\p{Alpha} ]+","");
                                            Log.e("Msg",Temp);
                                            final String[] positiveText = Temp.split("\\s");
                                            bayes.learn(item.getType().trim(), Arrays.asList(positiveText));
                                        }

                                    }
                                });
                            }
                        });

                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Intent mainActivity = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(mainActivity);
            }
        };
        thread.start();
        ft = fm.beginTransaction();
        ft.replace(R.id.frameLayout,new msg_list(),"Msg List");
        populate_adapter();
        ft.commit();
//        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
//            @Override
//            protected Void doInBackground(Void... params) {
//                try {
//
//                } catch (final Exception e) {
//                    Log.e("async", e.toString());
//                }
//
//                return null;
//            }
//        };
//        task.execute();
        //Toast.makeText(getApplicationContext(), "Learned", Toast.LENGTH_LONG).show();

    }
    @Override
    protected void onResume() {
        super.onResume();
        Runnable r = new Runnable() {
            @Override
            public void run(){
                managedCursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
                while (managedCursor.moveToNext()){
                    tempst = managedCursor.getString(managedCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    tempst = tempst.trim();
                    tempst = tempst.replaceAll("[^\\p{Digit}]+","");
                    if(tempst.length() > 10){tempst = tempst.substring(2);}
                    numbers.put(tempst,"on");
                }
            }
        };
        try {
            bayes = new BayesClassifier<String, String>();
            for(Spams item : result) {
                Temp = item.getText();
                Temp = Temp.trim();
                Temp = Temp.replaceAll("[^\\p{Alpha} ]+","");
                final String[] positiveText = Temp.split("\\s");
                bayes.learn(item.getType().trim(), Arrays.asList(positiveText));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //Toast.makeText(getApplicationContext(), "Learned", Toast.LENGTH_LONG).show();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {

            if (!Telephony.Sms.getDefaultSmsPackage(this).equals(getPackageName())) {

                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MainActivity.this);
                builder.setMessage("This app is not set as your default messaging app. Do you want to set it as default?")
                        .setCancelable(false)
                        .setTitle("Alert!")
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @TargetApi(19)
                            public void onClick(DialogInterface dialog, int id) {

                                Intent intent = new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
                                intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, getPackageName());
                                startActivity(intent);
                            }
                        });
                builder.show();
            }
        }

    }

    public void change_delete(View v) {
        l = 0;
        sv = v;
        idList = new String[j + 1];
        list = smsAdapter.getList();
        a = list.toArray();
        for (i = 0 ; i < a.length; i++){
            temp = (Data_SmsInbox)a[i];
            if(temp.getType().trim().equals("negative")){
                //Toast.makeText(getApplication(),temp.getMsg(),Toast.LENGTH_SHORT).show();
                idList[l] = temp.getId().trim();
                l++;
            }
        }
        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle("Alert");
        alertDialog.setMessage("are you sure?");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Delete " + (idList.length - 1) + " Spam Messages", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                for(i = 0 ; i  < idList.length ; i++){
                    deleteSMS(context,idList[i]);
                }
                populate_adapter();
                ft = fm.beginTransaction();
                ft.replace(R.id.frameLayout,new msg_list(),"Msg List");
                ft.commit();
                Toast.makeText(getApplication(),"Done",Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Toast.makeText(getApplication(),"No",Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
        alertDialog.show();
        ft = fm.beginTransaction();
        ft.replace(R.id.frameLayout,new Intro());
        ft.commit();
    }
    public void deleteSMS(Context context, String messageId) {
        try {
            context.getContentResolver().delete(
                    Uri.parse("content://sms/" + messageId), null, null);
            Log.e("Message:", "Message is Deleted successfully");
        }
        catch (Exception e) {
            Log.e("Exception", e.toString());
        }
    }


    public boolean contactExists(String number) {
        number = number.trim();
        number = number.replaceAll("[^\\p{Digit}]+","");
        if(number.length() > 10){number = number.substring(2);}
        if(numbers.get(number) != null){
            return true;
        }
        return false;
    }


}
