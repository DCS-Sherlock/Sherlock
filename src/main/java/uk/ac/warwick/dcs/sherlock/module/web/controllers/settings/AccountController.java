package uk.ac.warwick.dcs.sherlock.module.web.controllers.settings;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import uk.ac.warwick.dcs.sherlock.module.web.exceptions.NotAjaxRequest;
import uk.ac.warwick.dcs.sherlock.module.web.models.db.Account;
import uk.ac.warwick.dcs.sherlock.module.web.models.forms.AccountEmailForm;
import uk.ac.warwick.dcs.sherlock.module.web.models.forms.AccountNameForm;
import uk.ac.warwick.dcs.sherlock.module.web.models.forms.AccountPasswordForm;
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

	@GetMapping ("/account/name")
	public String nameFragmentGet(
			Model model,
			@ModelAttribute("isAjax") boolean isAjax,
			@ModelAttribute("account") Account account
	) throws NotAjaxRequest {
		if (!isAjax) throw new NotAjaxRequest("/account");

		model.addAttribute("accountNameForm", new AccountNameForm(account));
		return "settings/fragments/name";
	}

	@PostMapping ("/account/name")
	public String nameFragmentPost(
			@ModelAttribute("isAjax") boolean isAjax,
			@ModelAttribute("account") Account account,
			@Valid @ModelAttribute AccountNameForm accountNameForm,
			BindingResult result,
			Model model
	) throws NotAjaxRequest {
		if (!isAjax) throw new NotAjaxRequest("/account");

		if (!result.hasErrors()) {
			account.setUsername(accountNameForm.getUsername());
			accountRepository.save(account);

			model.addAttribute("success_msg", "account_updated_name");
		}

		return "settings/fragments/name";
	}

	@GetMapping ("/account/email")
	public String emailFragmentGet(
			Model model,
			@ModelAttribute("isAjax") boolean isAjax,
			@ModelAttribute("account") Account account
	) throws NotAjaxRequest {
		if (!isAjax) throw new NotAjaxRequest("/account");

		model.addAttribute("accountEmailForm", new AccountEmailForm(account));
		return "settings/fragments/email";
	}

	@PostMapping ("/account/email")
	public String emailFragmentPost(
			@ModelAttribute("isAjax") boolean isAjax,
			@ModelAttribute("account") Account account,
			@Valid @ModelAttribute AccountEmailForm accountEmailForm,
			BindingResult result,
			Model model
	) throws NotAjaxRequest {
		if (!isAjax) throw new NotAjaxRequest("/account");

		if (!result.hasErrors()) {
			if (bCryptPasswordEncoder.matches(accountEmailForm.getOldPassword(), account.getPassword())) {
				String oldEmail = account.getEmail();
				String newEmail = accountEmailForm.getEmail();

				//TODO: Email old + new addresses
				//TODO: Perform email verification

				//Update the name and email in the database
				account.setEmail(newEmail);
				accountRepository.save(account);

				//Update the email in the security session information to prevent the user being logged out.
				Collection<SimpleGrantedAuthority> authorities =
						(Collection<SimpleGrantedAuthority>) SecurityContextHolder.getContext().getAuthentication().getAuthorities();
				UsernamePasswordAuthenticationToken authentication =
						new UsernamePasswordAuthenticationToken(account.getEmail(), account.getPassword(), authorities);
				SecurityContextHolder.getContext().setAuthentication(authentication);

				model.addAttribute("success_msg", "account_updated_email");
			} else {
				result.reject("error_old_password_invalid");
			}
		}

		return "settings/fragments/email";
	}

	@GetMapping ("/account/password")
	public String passwordFragmentGet(
			Model model,
			@ModelAttribute("isAjax") boolean isAjax
	) throws NotAjaxRequest {
		if (!isAjax) throw new NotAjaxRequest("/account");

		model.addAttribute("accountPasswordForm", new AccountPasswordForm());
		return "settings/fragments/password";
	}

	@PostMapping ("/account/password")
	public String passwordFragmentPost(
			@ModelAttribute("isAjax") boolean isAjax,
			@ModelAttribute("account") Account account,
			@Valid @ModelAttribute AccountPasswordForm passwordForm,
			BindingResult result,
			Model model
	) throws NotAjaxRequest {
		if (!isAjax) throw new NotAjaxRequest("/account");

		if (!result.hasErrors()) {
			if (bCryptPasswordEncoder.matches(passwordForm.getOldPassword(), account.getPassword())) {
				account.setPassword(bCryptPasswordEncoder.encode(passwordForm.getNewPassword()));
				accountRepository.save(account);
				model.addAttribute("accountPasswordForm", new AccountPasswordForm());
				model.addAttribute("success_msg", "account_updated_password");
			} else {
				result.reject("error_old_password_invalid");
			}
		}

		return "settings/fragments/password";
	}
}