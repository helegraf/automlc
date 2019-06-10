package automlc.experiments.gaautomlc;

import org.aeonbits.owner.Config.LoadPolicy;
import org.aeonbits.owner.Config.LoadType;
import org.aeonbits.owner.Config.Sources;

import ai.libs.jaicore.experiments.IDatabaseConfig;
import ai.libs.jaicore.ml.experiments.IMultiClassClassificationExperimentConfig;

@LoadPolicy(LoadType.MERGE)
@Sources({ "file:conf/automlcExperimenter.properties", "file:conf/automlcdatabase.properties" })
public interface GAAutoMLCExperimenterConfig extends IMultiClassClassificationExperimentConfig, IDatabaseConfig {

}