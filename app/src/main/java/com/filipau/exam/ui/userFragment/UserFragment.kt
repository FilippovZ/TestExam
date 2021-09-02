package com.filipau.exam.ui.userFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import com.filipau.domain.dto.post.user.UserDto
import com.filipau.domain.outcome.Outcome
import com.filipau.exam.Constants.ERROR
import com.filipau.exam.Constants.ID_POST_KEY
import com.filipau.exam.MainActivity
import com.filipau.exam.R
import com.filipau.exam.base.mvvm.BaseMvvmView
import com.filipau.exam.databinding.FragmentUserBinding
import com.filipau.exam.ext.showAlertDialog
import org.koin.androidx.scope.ScopeFragment
import org.koin.androidx.viewmodel.ext.android.stateViewModel
import android.content.Intent
import android.net.Uri


class UserFragment : ScopeFragment(R.layout.fragment_user), BaseMvvmView {

    private var binding: FragmentUserBinding? = null
    private lateinit var postId: String
    private val viewModel: UserViewModel by stateViewModel()
    private var urlWeb = ""
    private var userPhone = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val toolbar = binding?.toolbar
        (requireActivity() as MainActivity).setSupportActionBar(toolbar)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUserBinding.inflate(inflater, container, false)
        postId = arguments?.getString(ID_POST_KEY) ?: ERROR
        binding?.postId?.text = postId

        binding?.userEmail?.setOnClickListener {
            val intent = Intent(Intent.ACTION_MAIN)
            intent.addCategory(Intent.CATEGORY_APP_EMAIL)
            requireActivity().startActivity(intent)
        }

        binding?.userWeb?.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            if (!urlWeb.startsWith("http://") && !urlWeb.startsWith("https://")) {
                urlWeb = "http://$urlWeb";
            }
            intent.data = Uri.parse(urlWeb)
            startActivity(intent)
        }

        binding?.userPhone?.setOnClickListener {

            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$userPhone"))
            startActivity(intent)

        }

        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getNewsFlow(postId).asLiveData(lifecycleScope.coroutineContext)
            .observe(viewLifecycleOwner, {
                when (it) {
                    is Outcome.Failure -> {
                        showError()
                    }

                    is Outcome.Progress -> {
                        if (it.loading) showProgress() else hideProgress()
                    }
                    is Outcome.Success -> {
                        showUserInfo(it.data)
                    }
                    else -> {
                        showError()
                    }
                }
            })
    }

    private fun showUserInfo(user: UserDto) {

        binding?.userId?.text = getString(R.string.contactId, user.id.toString())
        binding?.userName?.text = user.name
        binding?.nickname?.text = user.username
        binding?.userEmail?.text = user.email
        binding?.userWeb?.text = user.website
        urlWeb = user.website
        binding?.userPhone?.text = user.phone
        userPhone = user.phone
        binding?.userCity?.text = user.address.city

    }

    override fun showError() {
        hideProgress()
        activity?.showAlertDialog()
    }

    override fun showProgress() {
        binding?.progressBarUser?.visibility = View.VISIBLE
    }

    override fun hideProgress() {
        binding?.progressBarUser?.visibility = View.GONE
    }

}