package pl.finapi.paypal.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class StaticController {

	@RequestMapping(value = "/jak-to-dziala", method = RequestMethod.GET)
	public String getJakToDziala() {
		return "jak-to-dziala";
	}

	@RequestMapping(value = "/dla-ksiegowych", method = RequestMethod.GET)
	public String getDlaKsiegowych() {
		return "dla-ksiegowych";
	}

	@RequestMapping(value = "/dla-programistow", method = RequestMethod.GET)
	public String getApi() {
		return "dla-programistow";
	}

	@RequestMapping(value = "/o-nas", method = RequestMethod.GET)
	public String getONas() {
		return "o-nas";
	}

	@RequestMapping(value = "/blad")
	public String getBlad() {
		return "blad";
	}

}