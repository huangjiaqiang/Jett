package com.example.jett

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity()
{
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.btn_write).setOnClickListener { _ ->
            JettTest.writeTest(this);
            KryoTest.writeTest(this);
        }

        findViewById<Button>(R.id.btn_read).setOnClickListener { _ ->
//            JettTest.readTest(this);
            KryoTest.readTest(this);
        }
    }
}