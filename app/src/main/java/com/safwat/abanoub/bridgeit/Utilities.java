package com.safwat.abanoub.bridgeit;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

class Utilities {

    public static FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    public static FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

    public static FirebaseUser getCurrentUser() {
        return firebaseAuth.getCurrentUser();
    }

    public static String getCurrentEmail() {
        return getCurrentUser().getEmail();
    }

    public static String getCurrentUID() {
        return getCurrentUser().getUid();
    }

    public static String getCurrentUsername() {
        return getCurrentEmail().replace("@bridgeit.com", "");
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static boolean checkIfUserExist(DataSnapshot dataSnapshot, String currentUID) {

        ArrayList<String> list = new ArrayList<>();

        //iterate through each Msg, ignoring their UID
        if (dataSnapshot.getValue() != null) {
            for (DataSnapshot child : dataSnapshot.getChildren()) {

                list.add(child.getKey());
            }
        }
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).equals(currentUID))
                return true;
        }
        return false;
    }

    public static LatLng getBusLocation(DataSnapshot dataSnapshot) {

        LatLng latLng = null;
        //iterate through each Msg, ignoring their UID
        if (dataSnapshot.getValue() != null) {
            for (DataSnapshot child : dataSnapshot.getChildren()) {

                latLng = new LatLng(child.getValue(LatLng.class).latitude, child.getValue(LatLng.class).longitude);
            }
        }
        return latLng;
    }

    public static ArrayList<GalleryPicture> getAllGalleryPictures(DataSnapshot dataSnapshot) {

        ArrayList<GalleryPicture> list = new ArrayList<>();

        if (dataSnapshot.getValue() != null) {
            for (DataSnapshot child : dataSnapshot.getChildren()) {

                GalleryPicture galleryPicture = child.getValue(GalleryPicture.class);
                galleryPicture.pushID = child.getKey();

                list.add(galleryPicture);
            }
        }
        return list;
    }

    public static ArrayList<String> getAllYears(DataSnapshot dataSnapshot) {

        ArrayList<String> list = new ArrayList<>();
        if (dataSnapshot.getValue() != null) {
            for (DataSnapshot child : dataSnapshot.getChildren()) {
                list.add(child.getKey());
            }
        }
        return list;
    }

    public static ArrayList<SchoolClass> getAllClasses(DataSnapshot dataSnapshot) {

        ArrayList<SchoolClass> list = new ArrayList<>();
        if (dataSnapshot.getValue() != null) {
            for (DataSnapshot child : dataSnapshot.getChildren()) {

                SchoolClass schoolClass = new SchoolClass();
                schoolClass.className = child.getValue(SchoolClass.class).className;
                schoolClass.yearName = child.getValue(SchoolClass.class).yearName;

                list.add(schoolClass);
            }
        }
        return list;
    }

    public static ArrayList<Student> getStudentsFromUIDs(ArrayList<String> uid_list) {

        final ArrayList<Student> list = new ArrayList<>();

        for (int i = 0; i < uid_list.size(); i++) {
            firebaseDatabase.getReference().child("students").child(uid_list.get(i)).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {

                        Student student = new Student();
                        student.uid = dataSnapshot.getKey();
                        student.fullname = dataSnapshot.getValue(Student.class).fullname;
                        student.profileURL = dataSnapshot.getValue(Student.class).profileURL;
                        list.add(student);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        return list;
    }


    public static ArrayList<Msg> getAllMsgs(DataSnapshot dataSnapshot) {

        ArrayList<Msg> list = new ArrayList<>();

        if (dataSnapshot.getValue() != null) {
            for (DataSnapshot child : dataSnapshot.getChildren()) {

                Msg msgObj = new Msg();
                msgObj.uid = child.getKey();
                msgObj.sender = child.getValue(Msg.class).sender;
                msgObj.message = child.getValue(Msg.class).message;
                msgObj.photoUrl = child.getValue(Msg.class).photoUrl;
                list.add(msgObj);
            }
        }
        return list;
    }

    public static ArrayList<Teacher> getAllTeachers(DataSnapshot dataSnapshot) {

        ArrayList<Teacher> list = new ArrayList<>();

        if (dataSnapshot.getValue() != null) {
            for (DataSnapshot child : dataSnapshot.getChildren()) {

                Teacher teacher =child.getValue(Teacher.class);
                teacher.uid = child.getKey();

                list.add(teacher);
            }
        }
        return list;
    }

    public static ArrayList<String> getUIDs(DataSnapshot dataSnapshot) {

        ArrayList<String> list = new ArrayList<>();

        if (dataSnapshot.getValue() != null) {
            for (DataSnapshot child : dataSnapshot.getChildren()) {
                Object uid=child.child("uid").getValue();
//                if (uid !=null)
                    list.add(uid.toString());

//                String studentUID=child.getValue(Student.class).uid;
//                list.add(studentUID);
            }
        }
        return list;
    }

    public static ArrayList<Parent> getAllParents(DataSnapshot dataSnapshot) {

        ArrayList<Parent> list = new ArrayList<>();

        if (dataSnapshot.getValue() != null) {
            for (DataSnapshot child : dataSnapshot.getChildren()) {

                Parent parent = child.getValue(Parent.class);
                parent.uid = child.getKey();

                list.add(parent);
            }
        }
        return list;
    }

    public static ArrayList<DoctorReport> getAllDoctorReports(DataSnapshot dataSnapshot) {

        ArrayList<DoctorReport> list = new ArrayList<>();

        if (dataSnapshot.getValue() != null) {
            for (DataSnapshot child : dataSnapshot.getChildren()) {

                DoctorReport doctorReport = new DoctorReport();
                doctorReport.studentUID = child.getValue(DoctorReport.class).studentUID;
                doctorReport.title = child.getValue(DoctorReport.class).title;
                doctorReport.suggestedMedicine = child.getValue(DoctorReport.class).suggestedMedicine;
                doctorReport.diseaseDuration = child.getValue(DoctorReport.class).diseaseDuration;
                doctorReport.date = child.getValue(DoctorReport.class).date;

                list.add(doctorReport);
            }
        }
        return list;
    }

    public static ArrayList<Student> getAllStudents(DataSnapshot dataSnapshot) {

        ArrayList<Student> list = new ArrayList<>();

        if (dataSnapshot.getValue() != null) {
            for (DataSnapshot child : dataSnapshot.getChildren()) {

                Student student = child.getValue(Student.class);
                student.uid = child.getKey();

                list.add(student);
            }
        }
        return list;
    }

    public static Student getStudent(DataSnapshot dataSnapshot) {

        Student student = null;

        if (dataSnapshot.getValue() != null) {
            student = dataSnapshot.getValue(Student.class);
            student.uid = dataSnapshot.getKey();
        }
        return student;
    }

    public static String getStudentName(DataSnapshot dataSnapshot) {

        if (dataSnapshot.getValue() != null) {
            return dataSnapshot.getValue(Student.class).fullname;
        }
        return null;
    }

    public static String getUserFullName(DataSnapshot dataSnapshot) {

        if (dataSnapshot.getValue() != null) {
            Object fullname = dataSnapshot.child("fullname").getValue();
            if (fullname != null)
                return fullname.toString();
        }
        return null;
    }

    public static ArrayList<Complaint> getAllComplaints(DataSnapshot dataSnapshot) {

        ArrayList<Complaint> list = new ArrayList<>();

        if (dataSnapshot.getValue() != null) {
            for (DataSnapshot child : dataSnapshot.getChildren()) {

                Complaint complaint = child.getValue(Complaint.class);
                complaint.uid = child.getKey();

                list.add(complaint);
            }
        }
        return list;
    }

    public static String getComplaintStatus(DataSnapshot dataSnapshot) {
        if (dataSnapshot.child("status").getValue() != null) {
            return dataSnapshot.child("status").getValue().toString();
        }
        return null;
    }

    public static Parent getParent(DataSnapshot dataSnapshot) {

        Parent parent = null;

        if (dataSnapshot.getValue() != null) {
            parent = dataSnapshot.getValue(Parent.class);
            parent.uid = dataSnapshot.getKey();
        }
        return parent;
    }

    public static Teacher getTeacher(DataSnapshot dataSnapshot) {

        Teacher teacher = null;

        if (dataSnapshot.getValue() != null) {
            teacher = dataSnapshot.getValue(Teacher.class);
            teacher.uid = dataSnapshot.getKey();
        }
        return teacher;
    }

    public static ArrayList<StudentFeedback> getAllFeedbacks(DataSnapshot dataSnapshot) {

        ArrayList<StudentFeedback> list = new ArrayList<>();

        if (dataSnapshot.getValue() != null) {
            for (DataSnapshot child : dataSnapshot.getChildren()) {

                StudentFeedback studentFeedback = child.getValue(StudentFeedback.class);
                studentFeedback.uid = child.getKey();

                list.add(studentFeedback);
            }
        }
        return list;
    }

    public static ArrayList<String> getAllSubjects(DataSnapshot dataSnapshot) {
        ArrayList<String> list = new ArrayList<>();

        if (dataSnapshot.getValue() != null) {
            for (DataSnapshot child : dataSnapshot.getChildren()) {
                list.add(child.getKey());
            }
        }
        return list;
    }

    public static String getStudentClassID(DataSnapshot dataSnapshot) {

        if (dataSnapshot.getValue() != null)
            return dataSnapshot.getValue().toString();
        else
            return null;
    }

    public static Classroom getClassroom(DataSnapshot dataSnapshot) {

        Classroom classroom = new Classroom();

        if (dataSnapshot.getValue() != null) {
            classroom.id = dataSnapshot.getKey();
            classroom.yearName = dataSnapshot.getValue(Classroom.class).yearName;
            classroom.className = dataSnapshot.getValue(Classroom.class).className;
        }
        return classroom;
    }

    public static ArrayList<CalendarEvent> getAllCalendarEvents(DataSnapshot dataSnapshot) {
        ArrayList<CalendarEvent> list = new ArrayList<>();

        if (dataSnapshot.getValue() != null) {
            for (DataSnapshot child : dataSnapshot.getChildren()) {

                CalendarEvent calendarEvent = child.getValue(CalendarEvent.class);
                calendarEvent.uid = child.getKey();

                list.add(calendarEvent);
            }
        }
        return list;
    }

    public static Portfolio getPortfolioForSubject(DataSnapshot dataSnapshot) {
        Portfolio portfolio = new Portfolio();

        if (dataSnapshot.getValue() != null) {
            portfolio.subjectName = dataSnapshot.getKey();

            Object crowns = dataSnapshot.child("crown").getValue();
            if (crowns == null)
                portfolio.crown = "0";
            else
                portfolio.crown = crowns.toString();

            Object stars = dataSnapshot.child("star").getValue();
            if (stars == null)
                portfolio.star = "0";
            else
                portfolio.star = stars.toString();

            Object trophies = dataSnapshot.child("trophy").getValue();
            if (trophies == null)
                portfolio.trophy = "0";
            else
                portfolio.trophy = trophies.toString();

        } else
            portfolio = new Portfolio(dataSnapshot.getKey(), "0", "0", "0");

        return portfolio;
    }

    public static ArrayList<CompetitionGrades> getAllGrades(DataSnapshot dataSnapshot, ArrayList<String> uids_list) {

        ArrayList<CompetitionGrades> list = new ArrayList<>();

        if (dataSnapshot.getValue() != null) {
            for (DataSnapshot child : dataSnapshot.getChildren()) {

                CompetitionGrades competitionGrades = new CompetitionGrades();
                competitionGrades.title = child.getValue(CompetitionGrades.class).title;
                competitionGrades.grades_map = new HashMap<>();

                for (int i = 0; i < uids_list.size(); i++) {
                    String uid = uids_list.get(i);
                    Object grade = child.child(uid).child("grade").getValue();
                    if (grade == null)
                        competitionGrades.grades_map.put(uid, "0");
                    else
                        competitionGrades.grades_map.put(uid, grade.toString());
                }
                list.add(competitionGrades);
            }
        }
        return list;
    }

    public static boolean checkIfCompetitionExistInGrades(DataSnapshot dataSnapshot) {

        if (dataSnapshot.getValue() != null) {

            if (dataSnapshot.child("title").getValue() != null)
                return true;
            else
                return false;
        }
        return false;
    }

    public static boolean checkIfStudentGradeExistInCompetition(DataSnapshot dataSnapshot) {

        if (dataSnapshot.getValue() != null) {

            if (dataSnapshot.child("grade").getValue() != null)
                return true;
            else
                return false;
        }
        return false;
    }

    public static ArrayList<Competition> gettAllCompetitions(DataSnapshot dataSnapshot) {
        ArrayList<Competition> list = new ArrayList<>();

        if (dataSnapshot.getValue() != null) {
            for (DataSnapshot child : dataSnapshot.getChildren()) {

                Competition competition = child.getValue(Competition.class);
                competition.uid = child.getKey();

                list.add(competition);
            }
        }
        return list;
    }

    public static ArrayList<Announcement> getAllAnnouncements(DataSnapshot dataSnapshot) {
        ArrayList<Announcement> list = new ArrayList<>();

        if (dataSnapshot.getValue() != null) {
            for (DataSnapshot child : dataSnapshot.getChildren()) {

                Announcement announcement = child.getValue(Announcement.class);
                announcement.uid = child.getKey();

                list.add(announcement);
            }
        }
        return list;
    }

    public static ArrayList<Child> getAllChildrens(DataSnapshot dataSnapshot) {
        ArrayList<Child> list = new ArrayList<>();

        if (dataSnapshot.getValue() != null) {
            for (DataSnapshot child : dataSnapshot.getChildren()) {
                Child child1=child.getValue(Child.class);
                child1.uid_key=child.getKey();

                list.add(child1);
            }
        }
        return list;
    }

    //method to calculate the height of listview inside scrollview to show full list without need scrolling
    public static void getTotalHeightofListView(ListView listView) {

        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = listView.getPaddingTop() + listView.getPaddingBottom();
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.AT_MOST);
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);

            if (listItem != null) {
                // This next line is needed before you call measure or else you won't get measured height at all. The listitem needs to be drawn first to know the height.
                listItem.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
                listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
                totalHeight += listItem.getMeasuredHeight();
            }
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    //method to calculate the height of listview inside scrollview to show full list without need scrolling
    public static void getTotalHeightofListView2(ListView listView) {

        ListAdapter mAdapter = listView.getAdapter();

        int totalHeight = 0;

        for (int i = 0; i < mAdapter.getCount(); i++) {
            View mView = mAdapter.getView(i, null, listView);

            mView.measure(
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),

                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

            totalHeight += mView.getMeasuredHeight();

        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight
                + (listView.getDividerHeight() * (mAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    public static String getProfileURL(DataSnapshot dataSnapshot) {

        if (dataSnapshot.getValue() != null) {

            Object profileURL = dataSnapshot.child("profileURL").getValue();
            if (profileURL != null)
                return profileURL.toString();
        }
        return null;
    }

//    public static ArrayList<String> getAllLikersUIDs(DataSnapshot dataSnapshot) {
//
//        ArrayList<String> list = new ArrayList<>();
//
//        if (dataSnapshot.getValue() != null) {
//            for (DataSnapshot child : dataSnapshot.getChildren()) {
//
//                Object uid=child.child("uid").getValue();
//                if (uid !=null)
//                    list.add(uid.toString());
//            }
//        }
//        return list;
//    }
}