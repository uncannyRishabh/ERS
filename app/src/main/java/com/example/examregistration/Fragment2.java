package com.example.examregistration;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.github.barteksc.pdfviewer.PDFView;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;

public class Fragment2 extends Fragment {
    ImageView candidate_image;
    ProgressBar imageProgress;
    PDFView preview;
    Uri mImageUri,mPDFUri;
    Button submit;
    private TextInputLayout uploadDocs;
    private StorageTask mUploadTask,mUploadTask1;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseRef;
    private StorageReference mStorageRef;
    private static final int SELECT_IMAGE = 10;
    private static final int SELECT_PDF = 11;

    private String doc_typ = "";
    private String helperTxt = "PDF size must be lesser than 500KB";
    private Boolean isSelected = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tab2,container,false);
    }
    @Override
    public void onResume() {
        super.onResume();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(isSelected) {
                    uploadDocs.getStartIconDrawable().setTint(Color.parseColor("#FFA382FF"));
                }
            }
        },100);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        preview = view.findViewById(R.id.pdfPre);
        mAuth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("users");
        mStorageRef = FirebaseStorage.getInstance().getReference();

        final String[] doc_type = new String[] {"Adhaar","PAN","Voter ID"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, doc_type);
        AutoCompleteTextView textView = (AutoCompleteTextView) view.findViewById(R.id.doc_type_dropdown);
        textView.setAdapter(adapter);
        imageProgress = view.findViewById(R.id.Image_progress);
        if(imageProgress.getProgress()==0){
            imageProgress.setVisibility(View.INVISIBLE);
        }
        candidate_image = view.findViewById(R.id.upload_image_icon);
        candidate_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,SELECT_IMAGE);
            }
        });

        uploadDocs = view.findViewById(R.id.document_type);
        uploadDocs.setHelperText(helperTxt);
        uploadDocs.setStartIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                FirebaseUser currentUser = mAuth.getCurrentUser();
//                Toast.makeText(getContext(),"uid : "+currentUser.getUid(),Toast.LENGTH_SHORT).show();
                isSelected = true;
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("application/pdf");
                startActivityForResult(intent,SELECT_PDF);

            }
        });

        submit = view.findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(getContext(), ""+uploadDocs.getEditText().getText(), Toast.LENGTH_SHORT).show();
                if(mImageUri==null){
                    Snackbar.make(requireView(),"Select Image before submitting",Snackbar.LENGTH_SHORT).show();
                }
                if(mImageUri!=null && mPDFUri!=null && !uploadDocs.getEditText().getText().toString().equals("")){
                    doc_typ = uploadDocs.getEditText().getText()+"";
                    uploadPdf(helperTxt);
                    Intent i1 = new Intent(getActivity(),PaymentActivity.class);
                    i1.putExtra("pdfUri",mPDFUri.toString());
                    startActivity(i1);
                }
                else{
                    uploadDocs.setError("Select PDF and Document type");
                }
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==SELECT_IMAGE && resultCode==RESULT_OK && data!=null && data.getData()!=null) {
            mImageUri = data.getData();
            Log.d("TAG", "onActivityResult: "+mImageUri.getPath());
            candidate_image.setPadding(0,0,0,0);
            Glide.with(this).load(mImageUri).into(candidate_image);

            uploadImage();
//            Glide.with(requireContext()).load(downloadURL).into(delete);
        }

        if(requestCode==SELECT_PDF && resultCode==RESULT_OK && data!=null && data.getData()!=null) {
            mPDFUri = data.getData();
            String path = mPDFUri.getPath();
            File file = new File(path);
            helperTxt = file.getName();
            updateView(file.getName());

            if(preview.getVisibility()==View.GONE) {
                preview.setVisibility(View.VISIBLE);
                preview.fromUri(mPDFUri).enableSwipe(true).swipeHorizontal(true).load();

            }
            Toast.makeText(getContext(), file.getName()+" is selected", Toast.LENGTH_SHORT).show();
        }

    }

    private void updateView(String helperTxt) {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                uploadDocs.getStartIconDrawable().setTint(Color.parseColor("#FFA382FF"));
                uploadDocs.setHelperText(helperTxt);
            }
        },200);

    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = requireContext().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void uploadPdf(String filename) {
        if(mPDFUri!=null){
            StorageReference fileReference = mStorageRef.child("candidate docs/"+filename+System.currentTimeMillis());
            Handler handler = new Handler();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    mUploadTask1 = fileReference.putFile(mPDFUri)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                }
                            });

                    Task<Uri> urlTask = mUploadTask1.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }

                            // Continue with the task to get the download URL
                            return fileReference.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Uri downloadUri = task.getResult();
                                if (downloadUri == null) {
                                    Toast.makeText(getContext(), "TASK FAILED SUCCESSFULLY", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                else {
//                                    Toast.makeText(getContext(), "URL : "+downloadUri, Toast.LENGTH_SHORT).show();
                                    FirebaseUser currentUser = mAuth.getCurrentUser();
                                    Map<String,Object> update = new HashMap<>();
                                    update.put("docType",doc_typ);
                                    update.put("imageUrl",downloadUri.toString());
                                    mDatabaseRef.child(currentUser.getUid()).child("documents").child("candidate ID proof").updateChildren(update);
                                }

                            }
                        }
                    });
                }
            });
        }
    }

    private void updateProgress(int progress) {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(imageProgress.getProgress()==0){
                    imageProgress.setVisibility(View.INVISIBLE);
                }
                else {
                    imageProgress.setVisibility(View.VISIBLE);
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    imageProgress.setProgress(progress, true);
                } else {
                    imageProgress.setProgress(progress);
                }

            }
        },100);

    }

    private void uploadImage() {
        if (mImageUri != null) {
            StorageReference fileReference = mStorageRef.child("candidate pics/"+System.currentTimeMillis()
                    + "." + getFileExtension(mImageUri));
            Handler handler = new Handler();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    mUploadTask = fileReference.putFile(mImageUri)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Handler handler = new Handler();
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    updateProgress(0);
                                    try {
                                        Thread.sleep(150);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                            double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                            updateProgress((int) progress);
                            Log.d("TAG", "onProgress: "+progress);
                        }
                    });

                    Task<Uri> urlTask = mUploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }

                            // Continue with the task to get the download URL
                            return fileReference.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                imageProgress.setVisibility(View.GONE);
                                Uri downloadUri = task.getResult();
                                if (downloadUri == null) {
                                    Toast.makeText(getContext(), "TASK FAILED SUCCESSFULLY", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    Map<String,Object> upload = new HashMap<>();
                                    upload.put("imageUrl",downloadUri.toString());
                                    FirebaseUser currentUser = mAuth.getCurrentUser();
                                    mDatabaseRef.child(currentUser.getUid()).child("documents").child("candidate pic").updateChildren(upload);
                                }
                            }
                        }
                    });
                }
            });
        }
    }
}