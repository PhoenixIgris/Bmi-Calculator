package com.igris.bmicalculator


import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment

import com.igris.bmicalculator.databinding.FragmentMainBinding
import org.apache.poi.ss.usermodel.WorkbookFactory


class MainFragment : Fragment(R.layout.fragment_main) {
    private lateinit var binding: FragmentMainBinding
    private var isFemale: Boolean? = null
    private var englishSwitch: Boolean = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentMainBinding.bind(view)


        binding.apply {
            bmiValue.text = "0.0"
            bmiPerValue.text = "0.0"
            bmiResult.text = "Normal"
            btnCalc.setOnClickListener {
                calculate()

            }
            switchBtn.setOnClickListener {
                if (englishSwitch) {
                    englishSwitch = false
                    weightLy.setHelperText("lbs")
                    heightLy.setHelperText("inch")
                } else {
                    englishSwitch = true
                    weightLy.setHelperText("kilo")
                    heightLy.setHelperText("meter")
                }

            }

            femaleIcon.setOnClickListener {
                femaleIcon.setImageResource(R.drawable.feco)
                maleIcon.setImageResource(R.drawable.male)
                gender.setImageResource(R.drawable.febico)
                isFemale = true
            }
            maleIcon.setOnClickListener {

                maleIcon.setImageResource(R.drawable.maleco)
                femaleIcon.setImageResource(R.drawable.fe)
                gender.setImageResource(R.drawable.malebico)
                isFemale = false

            }

        }
    }


    private fun calculate() {
        if (binding.ageTxt.text.isNullOrEmpty()
            && binding.weightTxt.text.isNullOrEmpty()
            && binding.heightTxt.text.isNullOrEmpty()
            && isFemale == null
        ) {
            Toast.makeText(activity, "Please enter the fields", Toast.LENGTH_SHORT).show()
        } else if (binding.ageTxt.text.isNullOrEmpty()) {
            binding.ageLy.setError("Enter Age")

        } else if (binding.weightTxt.text.isNullOrEmpty()) {
            binding.weightLy.setError("Enter Weight")
        } else if (binding.heightTxt.text.isNullOrEmpty()) {
            binding.heightLy.setError("Enter Weight")
        } else if (isFemale == null) {
            Toast.makeText(activity, "Please select gender", Toast.LENGTH_SHORT).show()
        } else if (!(binding.ageTxt.text.isNullOrEmpty()
                    && binding.weightTxt.text.isNullOrEmpty()
                    && binding.heightTxt.text.isNullOrEmpty()
                    && isFemale != null)
        ) {
            binding.ageLy.isErrorEnabled = false
            binding.weightLy.isErrorEnabled = false
            binding.heightLy.isErrorEnabled = false
            val age: Double = binding.ageTxt.text.toString().toDouble() * 12.0
            var weight: Double = binding.weightTxt.text.toString().toDouble()
            var height: Double = binding.heightTxt.text.toString().toDouble()

            val isfemale: Boolean? = isFemale

            if (!englishSwitch) {

                weight = weight * 0.453592
                height = height * 0.0254
            }
            val bmivalue: Double = weight / (height * height)
            if (age > 240.5) {
                binding.pertCard.isVisible = false
                binding.chartAdult.isVisible = true
                binding.chartTeen.isVisible = false

                var category: String = when {
                    bmivalue < 18.5 -> "Under Weight"
                    bmivalue >= 18.5 && bmivalue < 25 -> "Healthy Weight"
                    bmivalue >= 25 && bmivalue < 30 -> "Over Weight"
                    bmivalue >= 30 && bmivalue < 35 -> "Obese (Class I)"
                    bmivalue >= 35 && bmivalue < 40 -> "Over (Class II)"
                    else -> "Obese (Class III)"
                }
                setBmiValues(bmivalue, "0.0", category)
            } else {
                binding.chartAdult.isVisible = false
                binding.chartTeen.isVisible = true
                readExcelData(age, isfemale, bmivalue)
            }
        } else {
            Toast.makeText(activity, "Some problem", Toast.LENGTH_SHORT).show()
        }
    }


    //good
    private fun readExcelData(age: Double?, isfemale: Boolean?, bmivalue: Double?) {

        val inputStream = requireActivity().assets.open("data.xls")
        val xlWb = WorkbookFactory.create(inputStream)
        var page = 0
        if (isfemale!!) {
            page = 0
        } else {
            page = 1
        }
        var xlWs = xlWb.getSheetAt(page)
        var xlRows = xlWs.rowIterator()
        var ans: Double? = null
        var L: Double? = null
        var M: Double? = null
        var S: Double? = null
        for (i in xlRows) {
            ans = i.getCell(1).toString().toDouble()
            if ((ans >= age!!)) {

                L = i.getCell(2).toString().toDouble()
                M = i.getCell(3).toString().toDouble()
                S = i.getCell(4).toString().toDouble()
                break
            }
        }

        var Z_Score = (((Math.pow((bmivalue!! / M!!), L!!)) - 1) / (L * S!!))


        val first = Z_Score.toString().substring(0, Z_Score.toString().indexOf(".") + 2)

        val second = Z_Score.toString()
            .substring(Z_Score.toString().indexOf(".") + 2, Z_Score.toString().indexOf(".") + 3)
            .toInt()





        xlWs = xlWb.getSheetAt(2)
        xlRows = xlWs.rowIterator()
        var answer: String? = null
        var percentile: String? = null
        for (i in xlRows) {
            answer = i.getCell(0).toString()
            if (answer == first) {
                percentile = i.getCell(second + 1).toString()
                break
            }
        }

        if (percentile!!.contains("%")) {
            percentile = percentile.replace("%", "")
        }
        var test: Double? = percentile!!.toDouble()
        var category: String = when {
            test!! < 5.0 -> "Under Weight"
            test!! >= 5.0 && test!! < 85.0 -> "Healthy Weight"
            test!! >= 85.0 && test!! < 95.0 -> "Over Weight"
            else -> "Obesity"
        }
        setBmiValues(bmivalue, percentile, category)


    }

    private fun setBmiValues(bmivalue: Double, percentile: String, category: String) {
        binding.apply {
            bmiValue.text = bmivalue.toString().substring(0, bmivalue.toString().indexOf(".") + 2)
            bmiPerValue.text = percentile
            bmiResult.text = category
        }


    }


}