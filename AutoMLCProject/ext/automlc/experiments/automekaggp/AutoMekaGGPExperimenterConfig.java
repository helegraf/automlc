package automlc.experiments.automekaggp;


import org.aeonbits.owner.Config.LoadPolicy;
import org.aeonbits.owner.Config.LoadType;
import org.aeonbits.owner.Config.Sources;

import jaicore.ml.experiments.IMultiClassClassificationExperimentConfig;

@LoadPolicy(LoadType.MERGE)
@Sources({ "file:conf/automekaggpExperimenter.properties", "file:conf/automekaggpdatabase.properties" })
public interface AutoMekaGGPExperimenterConfig extends IMultiClassClassificationExperimentConfig {

}