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
import uk.ac.warwick.dcs.sherlock.module.web.data.models.forms.AccountEmailForm;
import uk.ac.warwick.dcs.sherlock.module.web.data.models.forms.AccountNameForm;
import uk.ac.warwick.dcs.sherlock.module.web.data.models.forms.AccountPasswordForm;
import uk.ac.warwick.dcs.sherlock.module.web.data.wrappers.AccountWrapper;
import uk.ac.warwick.dcs.sherlock.module.web.data.repositories.AccountRepository;

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
		return "settings/account/index";
	}

	@GetMapping ("/account/name")
	public String nameGetFragment(
			Model model,
			@ModelAttribute("isAjax") boolean isAjax,
			@ModelAttribute("account") AccountWrapper account
	) throws NotAjaxRequest {
		if (!isAjax) throw new NotAjaxRequest("/account");

		model.addAttribute("accountNameForm", new AccountNameForm(account.getAccount()));
		return "settings/account/fragments/name";
	}

	@PostMapping ("/account/name")
	public String namePostFragment(
			@ModelAttribute("isAjax") boolean isAjax,
			@ModelAttribute("account") AccountWrapper account,
			@Valid @ModelAttribute AccountNameForm accountNameForm,
			BindingResult result,
			Model model
	) throws NotAjaxRequest {
		if (!isAjax) throw new NotAjaxRequest("/account");

		if (!result.hasErrors()) {
			account.getAccount().setUsername(accountNameForm.getUsername());
			accountRepository.save(account.getAccount());

			model.addAttribute("success_msg", "account.name.updated");
		}

		return "settings/account/fragments/name";
	}

	@GetMapping ("/account/email")
	public String emailGetFragment(
			Model model,
			@ModelAttribute("isAjax") boolean isAjax,
			@ModelAttribute("account") AccountWrapper account
	) throws NotAjaxRequest {
		if (!isAjax) throw new NotAjaxRequest("/account");

		model.addAttribute("accountEmailForm", new AccountEmailForm(account.getAccount()));
		return "settings/account/fragments/email";
	}

	@PostMapping ("/account/email")
	public String emailPostFragment(
			@ModelAttribute("isAjax") boolean isAjax,
			@ModelAttribute("account") AccountWrapper account,
			@Valid @ModelAttribute AccountEmailForm accountEmailForm,
			BindingResult result,
			Model model
	) throws NotAjaxRequest {
		if (!isAjax) throw new NotAjaxRequest("/account");

		if (!result.hasErrors()) {
			//Check that they are attempting to change the email
			if (!accountEmailForm.getEmail().equals(account.getEmail())) {
				//if attempting to change, check that the email doesn't already exist
				if (accountRepository.findByEmail(accountEmailForm.getEmail()) != null) {
					result.reject("error.email.exists");
					return "settings/account/fragments/email";
				}
			}

			String oldEmail = account.getEmail();
			String newEmail = accountEmailForm.getEmail();

			//TODO: Email old + new addresses
			//TODO: Perform email verification

			//Update the email in the database
			account.getAccount().setEmail(newEmail);
			accountRepository.save(account.getAccount());

			//Update the email in the security session information to prevent the user being logged out.
			Collection<SimpleGrantedAuthority> authorities =
					(Collection<SimpleGrantedAuthority>) SecurityContextHolder.getContext().getAuthentication().getAuthorities();
			UsernamePasswordAuthenticationToken authentication =
					new UsernamePasswordAuthenticationToken(account.getEmail(), account.getPassword(), authorities);
			SecurityContextHolder.getContext().setAuthentication(authentication);

			accountEmailForm.setOldPassword("");
			model.addAttribute("success_msg", "account.email.updated");
		}

		return "settings/account/fragments/email";
	}

	@GetMapping ("/account/password")
	public String passwordGetFragment(
			Model model,
			@ModelAttribute("isAjax") boolean isAjax
	) throws NotAjaxRequest {
		if (!isAjax) throw new NotAjaxRequest("/account");

		model.addAttribute("accountPasswordForm", new AccountPasswordForm());
		return "settings/account/fragments/password";
	}

	@PostMapping ("/account/password")
	public String passwordPostFragment(
			@ModelAttribute("isAjax") boolean isAjax,
			@ModelAttribute("account") AccountWrapper account,
			@Valid @ModelAttribute AccountPasswordForm accountPasswordForm,
			BindingResult result,
			Model model
	) throws NotAjaxRequest {
		if (!isAjax) throw new NotAjaxRequest("/account");

		if (!result.hasErrors()) {
			account.getAccount().setPassword(bCryptPasswordEncoder.encode(accountPasswordForm.getNewPassword()));
			accountRepository.save(account.getAccount());

			model.addAttribute("accountPasswordForm", new AccountPasswordForm());
			model.addAttribute("success_msg", "account.password.updated");
		}

		return "settings/account/fragments/password";
	}
}