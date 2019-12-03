package com.backend.boilerplate.dto.validator;

import javax.validation.GroupSequence;
import javax.validation.groups.Default;

/**
 * @author sarvesh
 * @version 0.0.1
 * @since 0.0.1
 */
@GroupSequence({Default.class, Extended.class})
public interface OwConstraintSequence {
}
