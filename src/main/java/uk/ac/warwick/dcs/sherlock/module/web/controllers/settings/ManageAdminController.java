package uk.ac.warwick.dcs.sherlock.module.web.controllers.settings;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import uk.ac.warwick.dcs.sherlock.module.web.exceptions.AccountNotFound;
import uk.ac.warwick.dcs.sherlock.module.web.exceptions.AccountOwner;
import uk.ac.warwick.dcs.sherlock.module.web.models.db.Account;
import uk.ac.warwick.dcs.sherlock.module.web.models.db.Role;
import uk.ac.warwick.dcs.sherlock.module.web.models.forms.AccountForm;
import uk.ac.warwick.dcs.sherlock.module.web.models.forms.PasswordForm;
import uk.ac.warwick.dcs.sherlock.module.web.models.wrapper.AccountWrapper;
import uk.ac.warwick.dcs.sherlock.module.web.repositories.AccountRepository;
import uk.ac.warwick.dcs.sherlock.module.web.repositories.RoleRepository;

import javax.validation.Valid;
import java.security.SecureRandom;
import java.util.Optional;
import java.util.Random;

@Controller
public class ManageAdminController {
    @Autowired
	private AccountRepository accountRepository;
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	@Autowired
	private RoleRepository roleRepository;

    public ManageAdminController() { }

	@GetMapping("/admin/manage/{pathid}")
	public String manageGet(
			@ModelAttribute("subAccount") Account subAccount,
			Model model
	) {
		model.addAttribute("accountForm", new AccountForm(subAccount));
		return "settings/admin/manage";
	}

	@PostMapping("/admin/manage/{pathid}")
	public String managePost(
			@Valid @ModelAttribute AccountForm accountForm,
			BindingResult result,
			@ModelAttribute("subAccount") Account subAccount,
			@ModelAttribute("account") AccountWrapper account,
			Model model
	) {
		if (!result.hasErrors()) {
			if (bCryptPasswordEncoder.matches(accountForm.getOldPassword(), account.getPassword())) {
				subAccount.setEmail(accountForm.getEmail());
				subAccount.setUsername(accountForm.getName());
				accountRepository.save(subAccount);

				boolean currentlyAdmin = false;
				Role currentRole = null;

				for (Role role : subAccount.getRoles()) {
					if (role.getName().equals("ADMIN")) {
						currentlyAdmin = true;
						currentRole = role;
					}
				}

				if (accountForm.isAdmin() && !currentlyAdmin) { //Add admin
					roleRepository.save(new Role("ADMIN", subAccount));
				}

				if (!accountForm.isAdmin() && currentlyAdmin) { //Remove admin
					roleRepository.delete(currentRole);
				}

				model.addAttribute("success_msg", "admin_account_updated_details");
				return "settings/admin/manage";
			} else {
				result.reject("error_old_password_invalid");
			}
		}

		return "settings/admin/manage";
	}

	@GetMapping("/admin/password/{pathid}")
	public String passwordGet(Model model) {
		model.addAttribute("passwordForm", new PasswordForm());
		return "settings/admin/password";
	}

	@PostMapping("/admin/password/{pathid}")
	public String passwordPost(
			@Valid @ModelAttribute PasswordForm passwordForm,
			BindingResult result,
			@ModelAttribute("subAccount") Account subAccount,
			@ModelAttribute("account") AccountWrapper account,
			Model model
	) {
		if (!result.hasErrors()) {
			if (bCryptPasswordEncoder.matches(passwordForm.getConfirmPassword(), account.getPassword())) {
				//Generate a random password
				final Random r = new SecureRandom();
				byte[] b = new byte[12];
				r.nextBytes(b);
				String newPassword = Base64.encodeBase64String(b);

				subAccount.setPassword(bCryptPasswordEncoder.encode(newPassword));
				accountRepository.save(subAccount);

				model.addAttribute("success_msg", "admin_accounts_change_password_confirm");
				model.addAttribute("newPassword", newPassword);
				return "settings/admin/passwordSuccess";
			} else {
				result.reject("error_old_password_invalid");
			}
		}

		return "settings/admin/password";
	}

	@GetMapping("/admin/delete/{pathid}")
	public String deleteGet(Model model) {
		model.addAttribute("passwordForm", new PasswordForm());
		return "settings/admin/delete";
	}

	@PostMapping("/admin/delete/{pathid}")
	public String deletePost(
			@Valid @ModelAttribute PasswordForm passwordForm,
			BindingResult result,
			@ModelAttribute("subAccount") Account subAccount,
			@ModelAttribute("account") AccountWrapper account,
			Model model
	) {
		if (!result.hasErrors()) {
			if (bCryptPasswordEncoder.matches(passwordForm.getConfirmPassword(), account.getPassword())) {
				accountRepository.delete(subAccount);
				return "redirect:/admin?msg=deleted";
			} else {
				result.reject("error_old_password_invalid");
			}
		}

		return "settings/admin/delete";
	}

	@ModelAttribute("subAccount")
	public Account getAccount(
            @ModelAttribute("account") AccountWrapper account,
            @PathVariable(value="pathid") long pathid,
            Model model)
        throws AccountOwner, AccountNotFound
    {
    	Optional<Account> optional = accountRepository.findById(pathid);

    	if (!optional.isPresent()) {
    		throw new AccountNotFound("Account not found");
		}

    	Account subAccount = optional.get();

    	if (subAccount.getId() == account.getId()) {
    		throw new AccountOwner("You are not allowed to modify your own account.");
		}

        model.addAttribute("subAccount", subAccount);
		return subAccount;
	}
}