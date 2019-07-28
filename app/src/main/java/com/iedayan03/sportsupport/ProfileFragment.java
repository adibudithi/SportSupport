package com.iedayan03.sportsupport;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import android.se.omapi.Session;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;



import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class ProfileFragment extends Fragment {
    User current;
    private SessionHandler session;
    SharedPreferences sp;

    @Nullable
    @Override


    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        session = new SessionHandler(getContext());
        current = session.getUserDetails();
        String name;

        if (session != null) {
            name = current.getUsername();
        } else {
            name = "does not display name";
        }

        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        TextView myName = view.findViewById(R.id.thisUser);
        myName.setText(name);
        Button deleter = view.findViewById(R.id.deleteAccount);
        deleter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                accountDelete(view);
                session.logOut();
                Intent intent = new Intent(ProfileFragment.this.getActivity(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });
        Button exit = view.findViewById(R.id.logOut);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                session.logOut();
                Intent intent = new Intent(ProfileFragment.this.getActivity(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        final Spinner position = view.findViewById(R.id.position);

        String[] items = new String[]{"GK", "LB", "RB","CD","DM","CM","CAM","LM","RM","ST"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, items);
        position.setAdapter(adapter);
        SharedPreferences sp = getActivity().getSharedPreferences("Position", Context.MODE_PRIVATE);
        int spinnerValue = sp.getInt("spinner_item",-1);
        if(spinnerValue != -1) {
            // set the value of the spinner
            position.setSelection(spinnerValue,true);
        }
        position.setOnItemSelectedListener(new OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int new_position, long id)
            {


                String selectedItem = parent.getItemAtPosition(new_position).toString();
                int selected_item =  position.getSelectedItemPosition();
                SharedPreferences sp = getActivity().getSharedPreferences("Position",0);
                SharedPreferences.Editor prefEditor = sp.edit();
                prefEditor.putInt("spinner_item", selected_item);
                prefEditor.commit();
                changePosition(selectedItem);

            }
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });

        return view;
    }
    public void accountDelete(View view){
      String username = current.getUsername();
      String password = current.getPassword();
      new accountDeleter(getActivity()).execute(username,password);
    }
    private class accountDeleter extends AsyncTask<String, Void, String> {
        Context context;
        private String userName;
        private String password;
        String delete_url = "http://iedayan03.web.illinois.edu/delete_account.php";
        public accountDeleter(Context ctx) {
            context = ctx;
        }
        @Override
        protected String doInBackground(String... params){
            try{
                userName = params[0];
                password = params[1];
                URL url = new URL(delete_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String post_data = URLEncoder.encode("Username","UTF-8")+"="+URLEncoder.encode(userName,"UTF-8")+"&"
                        + URLEncoder.encode("Password","UTF-8")+"="+URLEncoder.encode(password,"UTF-8");
                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"iso-8859-1"));
                String result="";
                String line;
                while((line = bufferedReader.readLine())!= null) {
                    result += line;
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                return result;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
    public void changePosition(String new_position){
        String username = current.getUsername();
        String password = current.getPassword();
        String position = new_position;
        new positionChanger(getActivity()).execute(position,username,password);
    }
    private class positionChanger extends AsyncTask<String, Void, String> {
        Context context;
        private String new_pos;
        private String userName;
        private String password;

        String position_url = "http://iedayan03.web.illinois.edu/change_position.php";
        public positionChanger(Context ctx) {
            context = ctx;
        }
        @Override
        protected String doInBackground(String... params){
            try{
                new_pos = params[0];
                userName = params[1];
                password = params[2];
                URL url = new URL(position_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String post_data = URLEncoder.encode("Position","UTF-8")+"="+URLEncoder.encode(new_pos,"UTF-8")+"&"
                        + URLEncoder.encode("Username","UTF-8")+"="+URLEncoder.encode(userName,"UTF-8")+"&"
                        + URLEncoder.encode("Password","UTF-8")+"="+URLEncoder.encode(password,"UTF-8");
                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"iso-8859-1"));
                String result="";
                String line;
                while((line = bufferedReader.readLine())!= null) {
                    result += line;
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                return result;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }


}
