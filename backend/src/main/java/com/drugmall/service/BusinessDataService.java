package com.drugmall.service;

import com.drugmall.vo.*;

import java.util.List;

public interface BusinessDataService {

    List<DepartmentVO> getDepartments();

    DepartmentConfigVO getDepartmentConfig(String departmentCode);

    List<DepartmentTagVO> getDepartmentTags();

    List<DictDataVO> getDictData(String typeCode);

    List<PaymentMethodVO> getPaymentMethods();

    List<ServiceShortcutVO> getServiceShortcuts();

    List<ConsultationStepVO> getConsultationSteps();
}
