package com.drugmall.admin.controller;

import com.drugmall.admin.common.Result;
import com.drugmall.admin.config.MockDataService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/finance")
@RequiredArgsConstructor
public class FinanceController {

    private final MockDataService mockDataService;

    @GetMapping("/statistics")
    public Result<JsonNode> getStatistics() {
        return Result.success(mockDataService.get("finance", "statistics"));
    }

    @GetMapping("/transactions")
    public Result<ObjectNode> getTransactions(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword) {
        ArrayNode txns = (ArrayNode) mockDataService.get("finance", "transactions");
        if (txns == null) return Result.success(mockDataService.getObjectMapper().createObjectNode());

        ArrayNode filtered = mockDataService.filterByKeyword(txns, keyword, "orderNo", "userName");
        filtered = mockDataService.filterByField(filtered, "type", type);
        filtered = mockDataService.filterByField(filtered, "status", status);
        return Result.success(mockDataService.paginate(filtered, pageNum, pageSize));
    }

    @GetMapping("/transactions/{id}")
    public Result<JsonNode> getTransactionDetail(@PathVariable String id) {
        ArrayNode txns = (ArrayNode) mockDataService.get("finance", "transactions");
        if (txns != null) {
            for (JsonNode t : txns) {
                if (id.equals(t.get("id").asText())) {
                    return Result.success(t);
                }
            }
        }
        return Result.error(404, "交易不存在");
    }

    @GetMapping("/withdrawals")
    public Result<ObjectNode> getWithdrawals(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword) {
        ArrayNode withdrawals = (ArrayNode) mockDataService.get("finance", "withdrawals");
        if (withdrawals == null) return Result.success(mockDataService.getObjectMapper().createObjectNode());

        ArrayNode filtered = mockDataService.filterByKeyword(withdrawals, keyword, "merchantName", "id");
        filtered = mockDataService.filterByField(filtered, "status", status);
        return Result.success(mockDataService.paginate(filtered, pageNum, pageSize));
    }

    @GetMapping("/withdrawals/stats")
    public Result<Object> getWithdrawalStats() {
        return Result.success(java.util.Map.of(
            "todayAmount", 5000.00, "todayCount", 1,
            "weekAmount", 18000.00, "weekCount", 3,
            "monthAmount", 36500.00, "monthCount", 6,
            "totalAmount", 256800.00, "totalCount", 48
        ));
    }

    @PostMapping("/withdrawals/{id}/audit")
    public Result<Void> auditWithdrawal(@PathVariable String id, @RequestBody JsonNode body) {
        return Result.success();
    }
}
