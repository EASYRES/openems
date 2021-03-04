package io.openems.edge.controller.ess.gridoptimizedselfconsumption;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(//
		name = "Controller Grid Optimized Self Consumption", //
		description = "")
@interface Config {

	@AttributeDefinition(name = "Component-ID", description = "Unique ID of this Component")
	String id() default "ctrlGridOptimizedSelfConsumption0";

	@AttributeDefinition(name = "Alias", description = "Human-readable name of this Component; defaults to Component-ID")
	String alias() default "";

	@AttributeDefinition(name = "Is enabled?", description = "Is this Component enabled?")
	boolean enabled() default true;

	@AttributeDefinition(name = "Ess-ID", description = "ID of Ess device.")
	String ess_id() default "ess0";

	@AttributeDefinition(name = "Grid-Meter-Id", description = "ID of the Grid-Meter.")
	String meter_id() default "meter0";

	@AttributeDefinition(name = "Maximum allowed Sell-To-Grid power", description = "The target limit for sell-to-grid power.")
	int maximumSellToGridPower();

	@AttributeDefinition(name = "Ess target filter", description = "This is auto-generated by 'Ess-ID'.")
	String ess_target() default "";

	@AttributeDefinition(name = "Meter target filter", description = "This is auto-generated by 'Grid-Meter-ID'.")
	String meter_target() default "";

	String webconsole_configurationFactory_nameHint() default "Controller Grid Optimized Self Consumption [{id}]";

}