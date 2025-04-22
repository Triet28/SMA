

package com.DADN.smartplantcare

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.DADN.smartplantcare.databinding.FragmentSettingsBinding
import com.google.firebase.auth.FirebaseAuth

class SettingsFragment : Fragment() {
    private lateinit var settingsFragmentBinding: FragmentSettingsBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        requireActivity().title = "Settings"

        settingsFragmentBinding = FragmentSettingsBinding.inflate(inflater, container, false)
        auth = FirebaseAuth.getInstance()

        val currentUser = auth.currentUser
        if (currentUser != null) {
            settingsFragmentBinding.userEmailTextView.text = currentUser.email
        }

        // Đăng xuất
        settingsFragmentBinding.logoutButton.setOnClickListener {
            auth.signOut()
            val intent = Intent(requireContext(), MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        // Đổi mật khẩu
        settingsFragmentBinding.changePasswordButton.setOnClickListener {
            showChangePasswordDialog()
        }

        return settingsFragmentBinding.root
    }

    private fun showChangePasswordDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Đổi mật khẩu")

        val input = EditText(requireContext())
        input.hint = "Nhập mật khẩu mới (tối thiểu 6 ký tự)"
        input.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        builder.setView(input)

        builder.setPositiveButton("Xác nhận") { dialog, _ ->
            val newPassword = input.text.toString().trim()
            if (newPassword.length < 6) {
                showToast("Mật khẩu phải có ít nhất 6 ký tự")
            } else {
                updatePassword(newPassword)
            }
            dialog.dismiss()
        }

        builder.setNegativeButton("Hủy") { dialog, _ -> dialog.dismiss() }

        builder.show()
    }

    private fun updatePassword(newPassword: String) {
        val user = auth.currentUser
        user?.updatePassword(newPassword)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    showToast("Đổi mật khẩu thành công")
                } else {
                    showToast("Đổi mật khẩu thất bại: ${task.exception?.message}")
                }
            }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}

