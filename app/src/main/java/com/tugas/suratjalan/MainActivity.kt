package com.tugas.suratjalan

import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kyanogen.signatureview.SignatureView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.add_barang.view.*
import kotlinx.android.synthetic.main.add_barang.view.buttonCancel
import kotlinx.android.synthetic.main.add_barang.view.buttonDone
import kotlinx.android.synthetic.main.create_signature.view.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {
    var tujuan = ""
    var email = ""
    var ttd1 = false
    var ttd2 = false
    private lateinit var layoutManager: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        val buttonItem = findViewById<ImageButton>(R.id.buttonItem)
        val linear = findViewById<LinearLayout>(R.id.linear)
        val textTujuan : EditText = findViewById(R.id.textTujuan)
        val textEmail = findViewById<EditText>(R.id.textEmail)
        val textPenerima = findViewById<EditText>(R.id.textPenerima)
        val barangList = ArrayList<Barang>()
        barangList.add(Barang(-1,"Nama Barang",-1, "Keterangan"))

        layoutManager = LinearLayoutManager(this)
        recycleView.layoutManager = layoutManager
        recycleView.adapter = BarangAdapter(barangList)

        buttonItem.setOnClickListener {
            val mDialogView = LayoutInflater.from(this).inflate(R.layout.add_barang, null)
            val mBuilder = AlertDialog.Builder(this).setView(mDialogView)
            val mAlertDialog = mBuilder.show()

            mDialogView.buttonDone.setOnClickListener {
                val name = mDialogView.textName.text.toString()
                val value = mDialogView.textValue.text.toString()
                val ket = mDialogView.textNote.text.toString()

                if (name.isEmpty())
                    mDialogView.textName.error = "Harus diisi"
                else if (value.isEmpty())
                    mDialogView.textValue.error = "Harus diisi"
                else{
                    barangList.add(Barang(barangList.size,name,value.toInt(),ket))
                    recycleView.adapter = BarangAdapter(barangList)
                    mAlertDialog.dismiss()
                }
            }
            mDialogView.buttonCancel.setOnClickListener {
                mAlertDialog.dismiss()
            }
        }

        img_signature.setOnClickListener {
            val mDialogView = LayoutInflater.from(this).inflate(R.layout.create_signature, null)
            val textSignature = mDialogView.findViewById<TextView>(R.id.textSignature)
            val signature_view = mDialogView.findViewById<SignatureView>(R.id.signature_view)
            val mBuilder = AlertDialog.Builder(this).setView(mDialogView)
            val  mAlertDialog = mBuilder.show()

            textSignature!!.text = "Tanda tangan penerima"

            mDialogView.buttonDone.setOnClickListener {
                if (signature_view.isBitmapEmpty) {
                    Toast.makeText(this, "please make any signature", Toast.LENGTH_SHORT).show();
                } else {
                    val bitmap = signature_view.signatureBitmap
                    img_signature.setImageBitmap(bitmap)
                    mAlertDialog.dismiss()
                    ttd1 = true
                }
            }

            mDialogView.buttonClear.setOnClickListener {
                signature_view.clearCanvas()
            }

            mDialogView.buttonCancel.setOnClickListener {
                mAlertDialog.dismiss()
            }
        }

        img_signature2.setOnClickListener {
            val mDialogView = LayoutInflater.from(this).inflate(R.layout.create_signature, null)
            val textSignature = mDialogView.findViewById<TextView>(R.id.textSignature)
            val signature_view = mDialogView.findViewById<SignatureView>(R.id.signature_view)
            val mBuilder = AlertDialog.Builder(this).setView(mDialogView)
            val  mAlertDialog = mBuilder.show()

            textSignature!!.text = "Tanda tangan Pengirim"
            mDialogView.buttonDone.setOnClickListener {
                if (signature_view.isBitmapEmpty) {
                    Toast.makeText(this, "please make any signature", Toast.LENGTH_SHORT).show();
                } else {
                    val bitmap = signature_view.signatureBitmap
                    img_signature2.setImageBitmap(bitmap)
                    mAlertDialog.dismiss()
                    ttd2 = true
                }
            }

            mDialogView.buttonClear.setOnClickListener {
                signature_view.clearCanvas()
            }

            mDialogView.buttonCancel.setOnClickListener {
                mAlertDialog.dismiss()
            }
        }

        button.setOnClickListener {
            tujuan = textTujuan.text.toString()
            email = textEmail.text.toString()
            textTujuan?.error = "This is error"

            if(textTujuan?.text.isNullOrEmpty()){ textTujuan?.setError("Tujuan harus diisi") }
            else if(textTujuan?.text.isNullOrEmpty()){ textEmail?.setError("Email harus diisi") }
            else if(barangList.size==1){ Toast.makeText(this, "Barang harus diisi", Toast.LENGTH_LONG).show() }
            else if(!ttd1){ Toast.makeText(this, "Tanda tangan Penerima", Toast.LENGTH_LONG).show() }
            else if(!ttd2){ Toast.makeText(this, "Tanda tangan Pengirim", Toast.LENGTH_LONG).show() }
            else if(textPenerima?.text.isNullOrEmpty()){ textPenerima?.setError("Nama penerima harus diisi") }
            else{
                val path = saveImage(takeScreenshotOfView(linear,linear.height,linear.width))
                sendEmail(email,"oku "+tujuan, path)
            }
        }
    }

    fun takeScreenshotOfView(view: View, height: Int, width: Int): Bitmap {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val bgDrawable = view.background
        if (bgDrawable != null) {
            bgDrawable.draw(canvas)
        } else {
            canvas.drawColor(Color.WHITE)
        }
        view.draw(canvas)
        return bitmap
    }

    private fun saveImage(finalBitmap:Bitmap):Uri {

        val root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                .toString() + File.separator + "Image_surat jalan"
        val myDir = File(root);
        myDir.mkdirs();
        val fname = "SJ "+tujuan+" - "+getDaysAgo()+".jpg";
        val file = File(myDir, fname);
        if (file.exists()) file.delete();
        Log.i("LOAD", root + fname);
        try {
            val out = FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
            Log.e("saveImage","sukses")
        } catch (e:Exception) {
            e.printStackTrace();
            Log.e("saveImage",e.message)
        }
        return Uri.fromFile(File(root, fname))
    }

    fun getDaysAgo(): String {
        var answer = ""
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val current = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")
            answer =  current.format(formatter)
            Log.d("answer",answer)
    } else {
        var date = Date();
        val formatter = SimpleDateFormat("dd.MM.yyyy HH:mm")
        answer = formatter.format(date)
        Log.d("answer",answer)
    }

    return answer
}

    private fun sendEmail(recipient: String, subject: String, uri:Uri) {
        val mIntent = Intent(Intent.ACTION_SEND)
        mIntent.data = Uri.parse("mailto:")
        mIntent.type = "text/plain"
        mIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(recipient))
        mIntent.putExtra(Intent.EXTRA_SUBJECT, subject)
        mIntent.putExtra(Intent.EXTRA_TEXT, "Surat Jalan OKU")
        mIntent.putExtra(Intent.EXTRA_STREAM, uri)

        try {
            startActivity(Intent.createChooser(mIntent, "Choose Email Client..."))
        }
        catch (e: Exception){
            Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
        }

    }
}
