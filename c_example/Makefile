################################################################################
#  Makefile
# Created on: Feb 25, 2019
#     Author: kychu
################################################################################

Project := example_kylink

RM    := @rm -rf
MKDIR := @mkdir

TopPath := $(shell pwd)
BuildPath := $(TopPath)/build

INCS := -I$(TopPath)/apps \
	-I$(TopPath)/kylink

LIBS := -lpthread

OBJS :=

# All of the sources participating in the build are defined here
-include kylink/subdir.mk
-include apps/subdir.mk

# Add inputs and outputs from these tool invocations to the build variables 

# All Target
all: $(Project) | $(BuildPath)

$(BuildPath):
	$(MKDIR) -p $@

$(OBJ_DIRS):
	$(MKDIR) -p $@

# Tool invocations
$(Project): $(OBJS)
	@echo ''
	@echo ' Building target $@'
	@gcc  -o $(Project) $(OBJS) $(LIBS)
	@echo ' Completed.'

# Other Targets
clean:
	-$(RM) $(BuildPath) $(Project)
	-@echo 'Cleaned.'

.PHONY: all clean

