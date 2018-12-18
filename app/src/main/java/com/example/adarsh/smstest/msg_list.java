package com.example.adarsh.smstest;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.microsoft.windowsazure.mobileservices.table.query.Query;
import com.microsoft.windowsazure.mobileservices.table.query.QueryOperations;

import java.util.List;
import java.util.concurrent.ExecutionException;


public class msg_list extends Fragment {
    final Classifier<String, String> bayes =
            new BayesClassifier<String, String>();
    int  i,l = 0;
    private MainActivity mainActivity;
    SQLiteDatabase sql;
    String[] idList;
    ListView lvMsg;
    SmsAdapter smsAdapter;
    Data_SmsInbox smsdata, temp;
    DbHelper db;
    List list;
    Object[] a;
    View sv;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        sv = inflater.inflate(R.layout.fragment_msg_list, container, false);
        lvMsg = (ListView)sv.findViewById(R.id.lvMsg);

        scan();
        return sv;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mainActivity = (MainActivity)context;
    }

    public void scan(){
        smsAdapter = MainActivity.smsAdapter;
        lvMsg.setAdapter(smsAdapter);
        list = smsAdapter.getList();
        a = list.toArray();
        idList = new String[MainActivity.j + 1];
        for (i = 0 ; i < a.length; i++){
            temp = (Data_SmsInbox)a[i];
            if(temp.getType().trim().equals("negative")){
                idList[l] = temp.getId().trim();
                l++;
            }
        }
        Toast.makeText(getActivity().getApplicationContext(),Integer.toString(MainActivity.j) + " Spam Messages Detected",Toast.LENGTH_SHORT).show();
        lvMsg.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(getContext(),"In onClick",Toast.LENGTH_SHORT).show();
                smsdata = (Data_SmsInbox) smsAdapter.getItem(position);
                try {
                    MainActivity.mMassageData = MainActivity.mClient.getSyncTable(Spams.class);
                    MainActivity.sync().get();
                    Query query = QueryOperations.field("deleted").eq(false);
                    MainActivity.result = MainActivity.mMassageData.read(query).get();
                    if(smsdata.getType().trim().equals("negative")){
                        smsdata.setType("positive");
                        MainActivity.j--;
                        for(Spams item : MainActivity.result){
                            item.setType("positive");
                            MainActivity.mMassageData.update(item);
                        }
                    }
                    else {
                        smsdata.setType("negative");
                        MainActivity.j++;
                        for(Spams item : MainActivity.result){
                            item.setType("negative");
                            MainActivity.mMassageData.update(item);
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                smsAdapter.notifyDataSetChanged();
                return;
            }
        });


    }
}
