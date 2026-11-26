//AuthenticationService.java
package com.drones.backend1PM.auth;

import com.drones.backend1PM.repository.ContaRepository;
import com.drones.backend1PM.entity.Conta;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import com.drones.backend1PM.config.JwtServiceGenerator;

@Service
public class LoginService {

	@Autowired
	private ContaRepository repository;
	@Autowired
	private JwtServiceGenerator jwtService;
	@Autowired
	private AuthenticationManager authenticationManager;


	
	public String logar(Login login) {

		String token = this.gerarToken(login);
		return token;

	}



	public String gerarToken(Login login) {
		authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(
						login.getUsername(),
						login.getPassword()
						)
				);
		Conta user = repository.findByNome(login.getUsername()).get();
		String jwtToken = jwtService.generateToken(user);
		return jwtToken;
	}


}
