package com.dip_int.test_webns.screens.in_app_camera

import android.Manifest
import android.content.ContentValues.TAG
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.util.Size
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.dip_int.test_webns.R
import com.dip_int.test_webns.api.callSyncImages
import com.dip_int.test_webns.common.ToastHelper
import com.dip_int.test_webns.common.dateFormatter
import com.dip_int.test_webns.common.rotateBitmapIfNeeded
import com.dip_int.test_webns.model.SyncImageResponse
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.common.util.concurrent.ListenableFuture
import retrofit2.Response
import java.io.File
import java.util.concurrent.Executors

/*** Created By Dipe K Das on: 13th Aug 2024*/

class InAppCameraActivity : AppCompatActivity() {

    companion object {
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CAMERA_PERMISSION = 1001
    }

    private var executor = Executors.newSingleThreadExecutor()
    private lateinit var previewView: PreviewView
    private lateinit var cameraProvider: ProcessCameraProvider
    private var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>? = null
    lateinit var captureBtn: Button
    lateinit var syncBtn: Button
    lateinit var progressCircular: ProgressBar
    private lateinit var switchCameraButton: FloatingActionButton
    lateinit var capturedImageViewCardView: CardView
    lateinit var cameraCardView: CardView
    lateinit var capturedImage: ImageView
    lateinit var back: ImageView
    private lateinit var imageCapture: ImageCapture
    private var isFrontCamera = true
    var capturedImageFromCamera: Uri? = null
    var photoFile: File? = null
    var syncResponse: Response<SyncImageResponse>?? = null

    override fun onResume() {
        super.onResume()
        println("onResume")

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_in_app_camera)
        back = findViewById(R.id.back)
        previewView = findViewById(R.id.previewView)
        captureBtn = findViewById(R.id.captureBtn)
        switchCameraButton = findViewById(R.id.switchCameraButton)
        capturedImageViewCardView = findViewById(R.id.capturedImageViewCardView)
        cameraCardView = findViewById(R.id.cameraCardView)
        capturedImage = findViewById(R.id.capturedImage)
        syncBtn = findViewById(R.id.syncBtn)
        progressCircular = findViewById(R.id.progress_circular)

        cameraCardView.visibility = View.VISIBLE
        capturedImageViewCardView.visibility = View.GONE

        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), REQUEST_CAMERA_PERMISSION)
        }

        clicks()
    }


    private fun allPermissionsGranted() = ContextCompat.checkSelfPermission(baseContext, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED

    private fun clicks() {
        back.setOnClickListener{
            finish()
        }

        captureBtn.setOnClickListener{
            if (capturedImageFromCamera != null) {
                capturedImageFromCamera = null
                captureBtn.text = "\uD83D\uDCF7      Capture"
                capturedImageViewCardView.visibility = View.GONE
                cameraCardView.visibility = View.VISIBLE
                switchCameraButton.visibility = View.VISIBLE
                startCamera()
            } else {
                progressCircular.visibility = View.VISIBLE
                captureImage()
            }
        }

        switchCameraButton.setOnClickListener {
            switchCamera()
        }

        syncBtn.setOnClickListener {
            if (capturedImageFromCamera != null) {
                progressCircular.visibility = View.VISIBLE
                callSyncImages(photoFile!!, "image123", "imageCaptureFromCam", this@InAppCameraActivity)

                Looper.myLooper()?.let { it1 ->
                    Handler(it1).postDelayed(Runnable {
                        progressCircular.visibility = View.GONE
                    }, 2500)
                }
            } else {
                ToastHelper.showErrorToast(this, "No Image Captured!")
            }
        }
    }

    private fun startCamera() {
        executor = Executors.newSingleThreadExecutor()
        cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture?.addListener({
            cameraProvider = cameraProviderFuture?.get()!!
            bindCameraUseCases(cameraProvider)
        }, ContextCompat.getMainExecutor(this))
    }

    private fun switchCamera() {
        stopCamera()
        isFrontCamera = !isFrontCamera

        when (isFrontCamera){
            true -> {
                switchCameraButton.tooltipText = "⟳   Switch To Back Camera"
                switchCameraButton.setImageResource(R.drawable.back_camera)
            }
            false -> {
                switchCameraButton.tooltipText = "⟳   Switch To Front Camera"
                switchCameraButton.setImageResource(R.drawable.frontcamera)
            }
        }

        startCamera()
    }

    private fun stopCamera() {
        cameraProvider.unbindAll()
    }

    private fun bindCameraUseCases(cameraProvider: ProcessCameraProvider) {
        val preview = Preview.Builder().build().also {
            it.setSurfaceProvider(previewView.surfaceProvider)
        }

        imageCapture = ImageCapture.Builder()
//            .setTargetAspectRatio(AspectRatio.RATIO_4_3)
            .setTargetResolution(Size(480, 720))
            .setJpegQuality(80)
            .build()

        val cameraSelector = if (isFrontCamera) {
            CameraSelector.DEFAULT_FRONT_CAMERA
        } else {
            CameraSelector.DEFAULT_BACK_CAMERA
        }

        cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)
    }

    private fun captureImage() {
        photoFile = File(outputDirectory, dateFormatter.format(System.currentTimeMillis()) + ".jpg")

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile!!).build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                    ToastHelper.showSuccessToast(baseContext, "Photo capture failed: ${exc.message}")
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    capturedImageFromCamera = output.savedUri ?: Uri.fromFile(photoFile)
                    val msg = "Photo captured"
                    ToastHelper.showSuccessToast(baseContext, msg)

                    capturedImageViewCardView.visibility = View.VISIBLE
                    cameraCardView.visibility = View.GONE
                    switchCameraButton.visibility = View.GONE
                    captureBtn.text = "↻   Re-Capture"
                    stopCamera()
                    progressCircular.visibility = View.GONE

                    updateImageView(capturedImageFromCamera)

                    Log.d(TAG, msg)
                    Log.d(TAG, "output.savedUri :: ${output.savedUri}")
                    Log.d(TAG, "output photoFile :: $photoFile")
                }
            }
        )
    }

    private fun updateImageView(imageUri: Uri?) {
        imageUri?.let {
            val imageStream = contentResolver.openInputStream(it)
            val bitmap = BitmapFactory.decodeStream(imageStream)
            val rotatedBitmap = rotateBitmapIfNeeded(this, bitmap, it)
            capturedImage.setImageBitmap(rotatedBitmap)
        }
    }

    private val outputDirectory: File get() = File(externalMediaDirs.firstOrNull()?.let {
        File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
    } ?: filesDir, "")

}