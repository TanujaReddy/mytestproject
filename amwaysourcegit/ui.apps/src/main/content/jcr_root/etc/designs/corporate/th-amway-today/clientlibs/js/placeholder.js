/*
 Placeholder Library
*/
$(document).ready(function(){function a(){""===$(this).val()&&$(this).val($(this).attr("placeholder")).addClass("placeholder")}function b(){$(this).val()===$(this).attr("placeholder")&&$(this).val("").removeClass("placeholder")}"placeholder"in $("<input>")[0]||($("input[placeholder], textarea[placeholder]").blur(a).focus(b).each(a),$("form").submit(function(){$(this).find("input[placeholder], textarea[placeholder]").each(b)}))});