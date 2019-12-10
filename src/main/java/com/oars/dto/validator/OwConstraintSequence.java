package com.oars.dto.validator;

import javax.validation.GroupSequence;
import javax.validation.groups.Default;


@GroupSequence({Default.class, Extended.class})
public interface OwConstraintSequence {
}
