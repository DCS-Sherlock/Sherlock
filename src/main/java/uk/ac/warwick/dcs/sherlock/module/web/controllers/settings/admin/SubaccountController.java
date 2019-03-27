package uk.ac.warwick.dcs.sherlock.module.web.controllers.settings.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import uk.ac.warwick.dcs.sherlock.module.web.configuration.SecurityConfig;
import uk.ac.warwick.dcs.sherlock.module.web.exceptions.AccountNotFound;
import uk.ac.warwick.dcs.sherlock.module.web.exceptions.AccountOwner;
import uk.ac.warwick.dcs.sherlock.module.web.data.models.db.Account;
import uk.ac.warwick.dcs.sherlock.module.web.data.models.db.Role;
import uk.ac.warwick.dcs.sherlock.module.web.data.models.forms.AccountForm;
import uk.ac.warwick.dcs.sherlock.module.web.data.models.forms.PasswordForm;
import uk.ac.warwick.dcs.sherlock.module.web.data.wrappers.AccountWrapper;
import uk.ac.warwick.dcs.sherlock.module.web.data.repositories.AccountRepository;
import uk.ac.warwick.dcs.sherlock.module.web.data.repositories.RoleRepository;

import javax.validation.Valid;
import java.util.Optional;

/**
 * The controller that deals with all the admin sub-account pages
 */
@Controller
public class SubaccountController {
	@Autowired
	private AccountRepository accountRepository;
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	@Autowired
	private RoleRepository roleRepository;

    /**
     * Handles GET requests to the manage account page
     *
     * @param subAccount the account being managed
     * @param model holder for model attributes
     *
     * @return the path to the manage account page
     */
	@GetMapping("/admin/manage/{pathid}")
	public String manageGet(
			@ModelAttribute("subAccount") AccountWrapper subAccount,
			Model model
	) {
		model.addAttribute("accountForm", new AccountForm(subAccount.getAccount()));
		return "settings/admin/manage";
	}

    /**
     * Handles POST requests to the manage account page, updates the
     * username, email and roles based on the form contents
     *
     * @param accountForm the form that should be submitted in the request
     * @param result the results of the validation on the form above
     * @param subAccount the account being managed
     * @param account the account wrapper for the logged in user
     * @param model holder for model attributes
     *
     * @return the path to the manage account page
     */
	@PostMapping("/admin/manage/{pathid}")
	public String managePost(
			@Valid @ModelAttribute AccountForm accountForm,
			BindingResult result,
			@ModelAttribute("subAccount") AccountWrapper subAccount,
			@ModelAttribute("account") AccountWrapper account,
			Model model
	) {
		if (!result.hasErrors()) {
		    //Check that they are attempting to change the email
		    if (!accountForm.getEmail().equals(subAccount.getEmail())) {
		        //if attempting to change, check that the email doesn't already exist
                if (accountRepository.findByEmail(accountForm.getEmail()) != null) {
                    result.reject("error.email.exists");
                    return "settings/admin/manage";
                }
            }

            subAccount.getAccount().setEmail(accountForm.getEmail());
            subAccount.getAccount().setUsername(accountForm.getName());
            accountRepository.save(subAccount.getAccount());

            boolean isAdmin = false; //whether or not the user is already an admin
            Role adminRole = null; //the admin role object for that account

            for (Role role : subAccount.getRoles()) {
                if (role.getName().equals("ADMIN")) {
                    isAdmin = true;
                    adminRole = role;
                }
            }

            if (accountForm.isAdmin() && !isAdmin) { //Add admin role
                roleRepository.save(new Role("ADMIN", subAccount.getAccount()));
            }

            if (!accountForm.isAdmin() && isAdmin) { //Remove admin role
                roleRepository.delete(adminRole);
            }

            model.addAttribute("success_msg", "admin.accounts.manage.updated");
		}

        accountForm.setOldPassword("");
		return "settings/admin/manage";
	}

    /**
     * Handles GET requests to the reset password page
     *
     * @param model holder for model attributes
     *
     * @return the path to the reset page
     */
	@GetMapping("/admin/password/{pathid}")
	public String passwordGet(Model model) {
		model.addAttribute("passwordForm", new PasswordForm());
		return "settings/admin/password";
	}

    /**
     * Handles POST requests to the reset password page
     *
     * @param passwordForm the form that should be submitted in the request
     * @param result the results of the validation on the form above
     * @param subAccount the account being managed
     * @param account the account wrapper for the logged in user
     * @param model holder for model attributes
     *
     * @return the path to the reset password page
     */
	@PostMapping("/admin/password/{pathid}")
	public String passwordPost(
			@Valid @ModelAttribute PasswordForm passwordForm,
			BindingResult result,
			@ModelAttribute("subAccount") AccountWrapper subAccount,
			@ModelAttribute("account") AccountWrapper account,
			Model model
	) {
		if (!result.hasErrors()) {
            //Generate a random password
            String newPassword = SecurityConfig.generateRandomPassword();

            subAccount.getAccount().setPassword(bCryptPasswordEncoder.encode(newPassword));
            accountRepository.save(subAccount.getAccount());

            model.addAttribute("success_msg", "admin.accounts.change_password.updated");
            model.addAttribute("newPassword", newPassword);
            return "settings/admin/passwordSuccess";
		}

		return "settings/admin/password";
	}

    /**
     * Handles GET requests to the delete account page
     *
     * @param model holder for model attributes
     *
     * @return the path to the delete page
     */
	@GetMapping("/admin/delete/{pathid}")
	public String deleteGet(Model model) {
		model.addAttribute("passwordForm", new PasswordForm());
		return "settings/admin/delete";
	}

    /**
     * Handles POST requests to the delete account page
     *
     * @param passwordForm the form that should be submitted in the request
     * @param result the results of the validation on the form above
     * @param subAccount the account being managed
     * @param account the account wrapper for the logged in user
     * @param model holder for model attributes
     *
     * @return the path to the delete page
     */
	@PostMapping("/admin/delete/{pathid}")
	public String deletePost(
			@Valid @ModelAttribute PasswordForm passwordForm,
			BindingResult result,
			@ModelAttribute("subAccount") AccountWrapper subAccount,
			@ModelAttribute("account") AccountWrapper account,
			Model model
	) {
		if (!result.hasErrors()) {
            accountRepository.delete(subAccount.getAccount());
            return "redirect:/admin?msg=deleted_account";
		}

		return "settings/admin/delete";
	}

    /**
     * Gets the account wrapper for the account where the id equals the
     * "pathid" attribute in the URL
     *
     * @param account the account object of the current user
     * @param pathid the account id from the path variable
     * @param model holder for model attributes
     *
     * @return the sub account wrapper
     *
     * @throws AccountOwner if the sub account is actually the account of the current user
     * @throws AccountNotFound if the account was not found
     */
	@ModelAttribute("subAccount")
	private AccountWrapper getAccount(
            @ModelAttribute("account") AccountWrapper account,
            @PathVariable(value="pathid") long pathid,
            Model model)
        throws AccountOwner, AccountNotFound
    {
    	Optional<Account> optional = accountRepository.findById(pathid);

    	if (!optional.isPresent()) {
    		throw new AccountNotFound("Account not found");
		}

    	AccountWrapper subAccount = new AccountWrapper(optional.get());

    	if (subAccount.getId() == account.getId()) {
    		throw new AccountOwner("You are not allowed to modify your own account.");
		}

        model.addAttribute("subAccount", subAccount);
		return subAccount;
	}
}