package com.example.concact;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    ArrayList<HashMap<String, String>> list=new ArrayList<HashMap<String,String>>();
    private ListView lv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lv = (ListView) findViewById(R.id.lv);
        initHuoQU();
    }
    public class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            ViewHander vh=null;
            if (convertView==null) {
                convertView=View.inflate(MainActivity.this, R.layout.item, null);
                vh=new ViewHander();
                vh.tv=(TextView) convertView.findViewById(R.id.tv);
                vh.tv2=(TextView) convertView.findViewById(R.id.tv2);
                vh.tv3=(TextView) convertView.findViewById(R.id.tv3);
                convertView.setTag(vh);
            }else {
                vh=(ViewHander) convertView.getTag();
            }
            vh.tv.setText(list.get(position).get("name"));
            vh.tv2.setText(list.get(position).get("phone"));
            vh.tv3.setText(list.get(position).get("note"));
            return convertView;
        }
        class ViewHander{
            TextView tv,tv2,tv3;
        }
    }
    private void initHuoQU() {
        //得到内存分析者
        ContentResolver contentResolver = getContentResolver();
        //用查询的方法里面几个参数一定不要写错
        //先查询RawContacts.CONTENT_URI表拿到联系人id
        Cursor query = contentResolver.query(ContactsContract.RawContacts.CONTENT_URI,new String[]{ContactsContract.RawContacts._ID}, null, null, null);
        //然后拿着联系人id去data表查询属于该联系人的信息
        while (query.moveToNext()){
            long id = query.getLong(0);
            HashMap<String, String> item = new HashMap();
            Cursor cursor2 = contentResolver.query(ContactsContract.Data.CONTENT_URI,
                    new String[]{ContactsContract.Data.DATA1, ContactsContract.Data.MIMETYPE},
                    ContactsContract.Data.RAW_CONTACT_ID + "=?",
                    new String[]{id + ""},
                    null);
            //得到data字段的值，就是联系人的信息，通过type判断是什么类型的信息
            while (cursor2.moveToNext()){
                String type = cursor2.getString(1);
                String data = cursor2.getString(0);
                //联系人姓名
                if (ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE.equals(type)){
                    item.put("name", data);
                }
                //联系人电话
                else if (ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE.equals(type)){
                    item.put("phone", data);
                }
                //联系人性别，用note标识
                else if (ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE.equals(type)){
                    item.put("note", data);
                }

            }

            list.add(item);
        }
        lv.setAdapter(new MyAdapter());
    }

}
