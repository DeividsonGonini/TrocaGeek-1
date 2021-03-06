package com.generation.trocageek.service;

import java.nio.charset.Charset;
import java.util.Optional;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.generation.trocageek.controller.form.UsuarioLogin;
import com.generation.trocageek.model.Usuario;
import com.generation.trocageek.repository.UsuarioRepository;

@Service
public class UsuarioService {

	@Autowired
	private UsuarioRepository repository;
	
	public Usuario cadastrarUsuario (Usuario usuario) {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		
		String senhaEncoder = encoder.encode(usuario.getSenha());
		usuario.setSenha(senhaEncoder);
		
		return repository.save(usuario);
	}
	
	public Optional<UsuarioLogin> logar (Optional<UsuarioLogin> user) {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		Optional<Usuario> usuario = repository.findByEmail(user.get().getEmail());
		if(usuario.isPresent()) {
			 if(encoder.matches(user.get().getSenha(), usuario.get().getSenha())) {
				 String auth = user.get().getEmail() + ":" + user.get().getSenha();
				 byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(Charset.forName("US-ASCII")));
				 String authHeader = "Basic " + new String(encodedAuth);
				 user.get().setToken(authHeader);
				 user.get().setNome(usuario.get().getNome());
				 user.get().setId(usuario.get().getId());
				 
				 return user;
			 }
		}
		
		return null;
		
	}
	
	public String gerarTokenNovo(String email, String senha) {
		String auth = email + ":" + senha;
		byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(Charset.forName("US-ASCII")));
		String authHeader = "Basic " + new String(encodedAuth);
		
		return authHeader;
		
		
	}
}
