package com.ETO.service;

import java.util.ArrayList;
import java.util.List;

import org.omg.CORBA.PRIVATE_MEMBER;
import org.omg.CORBA.PUBLIC_MEMBER;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.ETO.proj.TbSeller;
import com.ETO.sellergoods.service.SellerService;

public class UserDetailsServiceImpl implements UserDetailsService {
	private SellerService sellerService;

	public SellerService getSellerService() {
		return sellerService;
	}

	public void setSellerService(SellerService sellerService) {
		this.sellerService = sellerService;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		System.out.println("经过了认证类!");
		List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
		grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_SELLER"));
		TbSeller seller = sellerService.findOne(username);
		if(seller != null) {
			if(seller.getStatus().equals("1")) {
				return new User(username, seller.getPassword(), grantedAuthorities);
			}
		}
		return null;
	}

}
