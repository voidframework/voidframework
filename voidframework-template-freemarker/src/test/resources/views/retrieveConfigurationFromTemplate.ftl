<#if config("voidframework.core.runInDevMode")>
    Devel Model = TRUE
</#if>

${config("test.number")}
${config("test.string")}
${config("test.string")}
${config("test.object.a")?string.computer}
${config("test.object")}

<#list config("test.listString") as str>[${str}]</#list>
<#list config("test.listNumber") as num>[${num}]</#list>
<#list config("test.listBoolean") as boo>[${boo?then('Y', 'N')}]</#list>
<#list config("test.listObject") as obj>[${obj}]</#list>
