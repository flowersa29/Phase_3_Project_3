package com.ebookfrenzy.myapplication

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.label.ImageLabeler
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

class MainActivity : AppCompatActivity() {
    val REQUEST_IMAGE_CAPTURE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Build a button that launches the camera app
        findViewById<Button>(R.id.button).setOnClickListener {
            //TODO: Launch camera app

            //Create an intent that launches the camera and take a picture
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            try {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            } catch (e: ActivityNotFoundException) {
                // display error state to the user
            }

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            // Grab bitmap from image that was taken in camera
            val imageBitmap = data?.extras?.get("data") as Bitmap

            //set bitmap as imageView image
            findViewById<ImageView>(R.id.imageView).setImageBitmap(imageBitmap)

            // prepare bitmap for ML kit APIs
            val imageForMLKit = InputImage.fromBitmap(imageBitmap, 0 )

            // Utilize image labeling API
            val labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS)

            labeler.process(imageForMLKit)
                .addOnSuccessListener { labels ->
                    // Task completed successfully
                    Log.i("Aaron", "Successfully processed image")
                    for (label in labels) {
                        val text = label.text
                        // confidence of the label
                        val confidence = label.confidence
                        Log.i("Aaron", "detected: " + text + "with confidence: " + confidence)
                        findViewById<TextView>(R.id.resultsTextView2).text = "Detected: "  + text + "with confidence: " + confidence.toFloat()
                    }
                }
                .addOnFailureListener { e ->
                    // Task failed with an exception
                    Log.e("Aaron", "error processing image" )
                }

            //detect faces in an image
            val detector = FaceDetection.getClient()

            val result = detector.process(imageForMLKit)
                .addOnSuccessListener { faces ->
                    // Task completed successfully
                    Log.i("Aaron", "Faces detected:" +  faces.size)
                    findViewById<TextView>(R.id.resultsTextView).text = "Faces detected: " + faces.size
                }
                .addOnFailureListener { e ->
                    // Task failed with an exception

                }
            //Text recognition API
//            val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
//
//            val textRecognitionResult = recognizer.process(imageForMLKit)
//                .addOnSuccessListener { visionText ->
//                    // Task completed successfully
//                    // ...
//                }
//                .addOnFailureListener { e ->
//                    // Task failed with an exception
//                    // ...
//                }
        }
    }
}