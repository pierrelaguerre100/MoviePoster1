package com.example.poster1019;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ContactFragment extends Fragment {
    /** Creates the Contact Us page with its associated layout. */
    public ContactFragment(){
        super(R.layout.fragment_contact);
    }

    /** Connects the email row and personal-introduction button to their actions. */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.layoutEmail).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openEmailApp(getString(R.string.contact_email));
            }
        });

        Button personalIntroButton = view.findViewById(R.id.buttonPersonalIntro);
        TextView contactDescription = view.findViewById(R.id.textContactDescription);
        // Replaces the general contact text with the creator introduction on this same page.
        personalIntroButton.setOnClickListener(clickedView ->
                contactDescription.setText(R.string.personal_intro));
    }

    /** Opens an installed email application addressed to the app's contact email. */
    private void openEmailApp(String emailAddress){
        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:"+emailAddress));
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(requireContext(),R.string.no_email_app,Toast.LENGTH_SHORT).show();
        }
    }
}
