package com.pccoe.evcharging.slot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.pccoe.evcharging.MainActivity;
import com.pccoe.evcharging.databinding.ActivityNotifyBinding;
import com.pccoe.evcharging.models.History;
import com.pccoe.evcharging.models.Notification;

import java.util.List;

public class NotifyActivity extends AppCompatActivity {

    List<Notification> notificationList;
    Notification notification;
    String owner_email="";
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private ActivityNotifyBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityNotifyBinding.inflate(getLayoutInflater());

        init();

        getOwnerNotificationDetails();

        setContentView(binding.getRoot());
    }
//User Side
// Add this when user wants reserve slot but slot is not available
// owner notification  details from (Notification Collection)
// add user

//Owner Side
// Add this when owner will update status to available
// send notifications to all users delete its all previous entries
// delete previous notify of that owner

    //User Side
    private void getOwnerNotificationDetails(){ //get owner details
        firebaseFirestore
                .collection("Notification")
                .document(owner_email)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot doc) {
                        notification = doc.toObject(Notification.class);
                        addUserNotify();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(NotifyActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void addUserNotify(){ //add user
        firebaseFirestore
                .collection("Owner")
                .document(owner_email)//owner_email
                .collection("Notify")
                .document(notification.getNoti_email())
                .set(notification)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(NotifyActivity.this, "You Will Get Notified when Station will be available", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(NotifyActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void getAllUsers(){ //onsetAvailability to available
        firebaseFirestore
                .collection("Owner")
                .document(owner_email)//owner_email
                .collection("Notify")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot snaps) {
                        if (snaps == null) return;
                        notificationList.addAll(snaps.toObjects(Notification.class));

                        for(Notification i: notificationList){ //iterate through all pending notifications
                            notifyUser(i);
                        }


                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(NotifyActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void notifyUser(Notification notification){ //notify user
//write logic to notify user you have its email and token
    }

    private void deletePreviousEntries(){
        firebaseFirestore
                .collection("Owner")
                .document(owner_email)//owner_email
                .collection("Notify")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot snaps) {

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(NotifyActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void init(){
        notification = null;

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
    }
}