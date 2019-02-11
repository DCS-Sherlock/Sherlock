package uk.ac.warwick.dcs.sherlock.module.web.controllers.settings;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import uk.ac.warwick.dcs.sherlock.module.web.exceptions.NotAjaxRequest;
import uk.ac.warwick.dcs.sherlock.module.web.models.db.Account;
import uk.ac.warwick.dcs.sherlock.module.web.models.forms.AccountForm;
import uk.ac.warwick.dcs.sherlock.module.web.models.forms.PasswordForm;
import uk.ac.warwick.dcs.sherlock.module.web.repositories.AccountRepository;

import javax.validation.Valid;
import java.util.Collection;

@Controller
public class AccountController {
	@Autowired
	private AccountRepository accountRepository;
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@GetMapping ("/account")
	public String indexGet() {
		return "settings/account";
	}

	@GetMapping ("/account/details")
	public String detailsFragmentGet(
			Model model,
			@ModelAttribute("isAjax") boolean isAjax,
			@ModelAttribute("account") Account account
	) throws NotAjaxRequest {
		if (!isAjax) throw new NotAjaxRequest("/account");

		model.addAttribute("accountForm", new AccountForm(account));
		return "settings/fragments/details";
	}

	@PostMapping ("/account/details")
	public String detailsFragmentPost(
			@ModelAttribute("isAjax") boolean isAjax,
			@ModelAttribute("account") Account account,
			@Valid @ModelAttribute AccountForm accountForm,
			BindingResult result,
			Model model
	) throws NotAjaxRequest {
		if (!isAjax) throw new NotAjaxRequest("/account");

		if (!result.hasErrors()) {
			if (bCryptPasswordEncoder.matches(accountForm.getOldPassword(), account.getPassword())) {
				String oldEmail = account.getEmail();
				String newEmail = accountForm.getEmail();

				//TODO: Email old + new addresses
				//TODO: Perform email verification

				//Update the name and email in the database
				account.setEmail(newEmail);
				account.setName(accountForm.getName());
				accountRepository.save(account);

				//Update the email in the security session information to prevent the user being logged out.
				Collection<SimpleGrantedAuthority> authorities =
						(Collection<SimpleGrantedAuthority>) SecurityContextHolder.getContext().getAuthentication().getAuthorities();
				UsernamePasswordAuthenticationToken authentication =
						new UsernamePasswordAuthenticationToken(account.getEmail(), account.getPassword(), authorities);
				SecurityContextHolder.getContext().setAuthentication(authentication);

				model.addAttribute("success_msg", "account_updated_details");
			} else {
				result.reject("error_old_password_invalid");
			}
		}

		return "settings/fragments/details";
	}

	@GetMapping ("/account/password")
	public String passwordFragmentGet(
			Model model,
			@ModelAttribute("isAjax") boolean isAjax
	) throws NotAjaxRequest {
		if (!isAjax) throw new NotAjaxRequest("/account");

		model.addAttribute("passwordForm", new PasswordForm());
		return "settings/fragments/password";
	}

	@PostMapping ("/account/password")
	public String passwordFragmentPost(
			@ModelAttribute("isAjax") boolean isAjax,
			@ModelAttribute("account") Account account,
			@Valid @ModelAttribute PasswordForm passwordForm,
			BindingResult result,
			Model model
	) throws NotAjaxRequest {
		if (!isAjax) throw new NotAjaxRequest("/account");

		if (!result.hasErrors()) {
			if (bCryptPasswordEncoder.matches(passwordForm.getOldPassword(), account.getPassword())) {
				account.setPassword(bCryptPasswordEncoder.encode(passwordForm.getNewPassword()));
				accountRepository.save(account);
				model.addAttribute("success_msg", "account_updated_password");
			} else {
				result.reject("error_old_password_invalid");
			}
		}

		return "settings/fragments/password";
	}
}